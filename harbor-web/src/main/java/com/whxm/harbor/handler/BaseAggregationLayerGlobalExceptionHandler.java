package com.whxm.harbor.handler;


import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import com.whxm.harbor.bean.ParameterInvalidItem;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ExceptionEnum;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.BusinessException;
import com.whxm.harbor.utils.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @updateBy Ftibw 2018/6/28 02:37 AM
 * @desc 聚合层全局异常处理类
 */
public class BaseAggregationLayerGlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAggregationLayerGlobalExceptionHandler.class);

    /**
     * 违反约束异常
     */
    protected Result handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        LOGGER.info("handleConstraintViolationException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtil.convertCVSetToParameterInvalidItemList(e.getConstraintViolations());
        return Result.failure(ResultEnum.PARAM_IS_INVALID, parameterInvalidItemList);
    }

    /**
     * 处理验证参数封装错误时异常
     */
    protected Result handleConstraintViolationException(HttpMessageNotReadableException e, HttpServletRequest request) {
        LOGGER.info("handleConstraintViolationException start, uri:{}, caused by: ", request.getRequestURI(), e);
        return Result.failure(ResultEnum.PARAM_IS_INVALID, e.getMessage());
    }

    /**
     * 处理参数绑定时异常（反400错误码）
     */
    protected Result handleBindException(BindException e, HttpServletRequest request) {
        LOGGER.info("handleBindException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtil.convertBindingResultToMapParameterInvalidItemList(e.getBindingResult());
        return Result.failure(ResultEnum.PARAM_IS_INVALID, parameterInvalidItemList);
    }

    /**
     * 处理使用@Validated注解时，参数验证错误异常（反400错误码）
     */
    protected Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.info("handleMethodArgumentNotValidException start, uri:{}, caused by: ", request.getRequestURI(), e);
        List<ParameterInvalidItem> parameterInvalidItemList = ConvertUtil.convertBindingResultToMapParameterInvalidItemList(e.getBindingResult());
        return Result.failure(ResultEnum.PARAM_IS_INVALID, parameterInvalidItemList);
    }

    /**
     * 处理通用自定义业务异常
     */
    protected ResponseEntity<Result> handleBusinessException(BusinessException e, HttpServletRequest request) {
        LOGGER.info("handleBusinessException start, uri:{}, exceptions:{}, caused by: {}", request.getRequestURI(), e.getClass(), e.getMessage());
        //根据异常的真实类型,获取限定死的的异常枚举常量,仅仅是为了从找到的常量中设置响应的HTTP状态码
        ExceptionEnum ee = ExceptionEnum.getByEClass(e.getClass());
        if (ee != null) {
            return ResponseEntity
                    .status(ee.getHttpStatus())
                    .body(Result.failure(e.getResultEnum(), e.getData()));
        }
        //e为BusinessException类本身的实例时 ExceptionEnum == null,
        //其成员属性code/message/data/resultEnum均有可能为null
        return ResponseEntity
                .status(HttpStatus.OK)
                //成员resultEnum=null,说明异常发生情况无法与ResultEnum中定义的任意一种匹配,默认为系统内部错误...
                //成员resultEnum!=null,说明是自己手动配置的各个成员属性
                .body(e.getResultEnum() == null ? Result.failure(ResultEnum.SYSTEM_INNER_ERROR) : Result.failure(e.getResultEnum(), e.getData()));
    }

    /**
     * 处理运行时系统异常（反500错误码）
     */
    protected Result handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("handleRuntimeException start, uri:{}, caused by:", request.getRequestURI(), e);
        //TODO 可通过邮件、微信公众号等方式发送信息至开发人员、记录存档等操作
        return Result.failure(ResultEnum.SYSTEM_INNER_ERROR);
    }

    /*
     * 处理数据库键完整性约束异常
     */
    protected ResponseEntity<Result> handleConstraintViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        LOGGER.info("handleConstraintViolationException start, uri:{}, caused by: ", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, dataIntegrityMessageFormat(e.getMessage())));
    }

    private String dataIntegrityMessageFormat(String message) {

        String ret = null;
        //外键约束异常
        Pattern pattern = Pattern.compile(
                "### Cause.*Cannot delete or update a parent row: (a foreign key constraint fails \\((.*\\.`(.*)`, CONSTRAINT.*REFERENCES `(.*)` \\(.*)\\))"
        );

        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {

            ret = matcher.group(3) + "使用了" + matcher.group(4) + "中的数据," + matcher.group(4) + "数据无法删除";
            //System.out.println(matcher.group(3) + "使用了" + matcher.group(4) + "中的数据," + matcher.group(4) + "数据无法删除");
        }

        //键重复异常
        if (null == ret) {

            Pattern pattern2 = Pattern.compile(
                    "### Cause.*Duplicate entry '(.*)' for key '(.*)'"
            );

            Matcher matcher2 = pattern2.matcher(message);

            while (matcher2.find()) {

                ret = "值为[" + matcher2.group(1) + "]的[" + matcher2.group(2) + "]重复";
            }
        }

        return ret;
    }

    public static void main(String[] args) {
        String msg = "\r\n" +
                "### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`harbor`.`biz_map`, CONSTRAINT `biz_map_ibfk_1` FOREIGN KEY (`floor_id`) REFERENCES `biz_floor` (`floor_id`))" +
                "\r\n" +
                "### The error may involve com.whxm.harbor.mapper.BizFloorMapper.deleteByPrimaryKey-Inline" +
                "\r\n" +
                "### The error occurred while setting parameters" +
                "\r\n" +
                "### SQL: delete from biz_floor     where floor_id = ?" +
                "\r\n" +
                "### Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`harbor`.`biz_map`, CONSTRAINT `biz_map_ibfk_1` FOREIGN KEY (`floor_id`) REFERENCES `biz_floor` (`floor_id`))" +
                "\n" +
                "; SQL []; Cannot delete or update a parent row: a foreign key constraint fails (`harbor`.`biz_map`, CONSTRAINT `biz_map_ibfk_1` FOREIGN KEY (`floor_id`) REFERENCES `biz_floor` (`floor_id`)); nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`harbor`.`biz_map`, CONSTRAINT `biz_map_ibfk_1` FOREIGN KEY (`floor_id`) REFERENCES `biz_floor` (`floor_id`))";

        Pattern pattern = Pattern.compile(
                "### Cause.*Cannot delete or update a parent row: (a foreign key constraint fails \\((.*\\.`(.*)`, CONSTRAINT.*REFERENCES `(.*)` \\(.*)\\))"
        );

        Matcher matcher = pattern.matcher(msg);

        while (matcher.find()) {
            System.out.println(matcher.group(3) + "使用了" + matcher.group(4) + "中的数据," + matcher.group(4) + "数据无法删除");
        }

        String detail = "### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'A0003' for key 'biz_format_number'\n" +
                "### The error may involve com.whxm.harbor.mapper.BizFormatMapper.updateByPrimaryKeySelective-Inline\n" +
                "### The error occurred while setting parameters\n" +
                "### SQL: update biz_format          SET biz_format_type = ?,                                           biz_format_number = ?          where biz_format_id = ?\n" +
                "### Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'A0003' for key 'biz_format_number'\n" +
                "; SQL []; Duplicate entry 'A0003' for key 'biz_format_number'";

        Pattern pattern2 = Pattern.compile(
                "### Cause.*Duplicate entry '(.*)' for key '(.*)'"
        );

        Matcher matcher2 = pattern2.matcher(detail);

        while (matcher2.find()) {
            System.out.println("值为[" + matcher2.group(1) + "]的[" + matcher2.group(2) + "]重复");
        }
    }
}
