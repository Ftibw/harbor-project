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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

/**
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

        ExceptionEnum ee = ExceptionEnum.getByEClass(e.getClass());
        if (ee != null) {
            return ResponseEntity
                    .status(ee.getHttpStatus())
                    .body(Result.failure(ee.getResultEnum(), e.getData()));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(e.getResultEnum() == null ? Result.failure(e.getResultEnum()) : Result.failure(e.getResultEnum(), e.getData()));
    }

    /**
     * 处理运行时系统异常（反500错误码）
     */
    protected Result handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        LOGGER.error("handleRuntimeException start, uri:{}, caused by: ", request.getRequestURI(), e);
        //TODO 可通过邮件、微信公众号等方式发送信息至开发人员、记录存档等操作
        return Result.failure(ResultEnum.SYSTEM_INNER_ERROR);
    }

}
