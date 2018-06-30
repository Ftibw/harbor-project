package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ExceptionEnum;
import com.whxm.harbor.enums.ResultEnum;
import org.apache.commons.lang3.StringUtils;


/**
 * @author zhumaer
 * @desc 业务异常类
 * @updateBy Ftibw 2018/6/28 23:47 PM
 * @since 9/18/2017 3:00 PM
 */
public class BusinessException extends RuntimeException {

    protected Integer code;

    protected String message;

    protected ResultEnum resultEnum;

    protected Object data;

    public BusinessException() {
        ExceptionEnum exceptionEnum = ExceptionEnum.getByEClass(this.getClass());
        if (exceptionEnum != null) {
            resultEnum = exceptionEnum.getResultEnum();
            code = exceptionEnum.getResultEnum().code();
            //这个message是resultEnum常量中的死信息,不够详细
            //因为全局异常处理最后返回给前端的code和message
            //都来自BusinessException的resultEnum属性,仅有data时自己设置的
            //需要在调用getResultEnum()时,将BusinessException中的message覆盖到resultEnum中去
            message = exceptionEnum.getResultEnum().message();
        }

    }

    public BusinessException(String message) {
        this();
        this.message = message;
    }

    public BusinessException(String format, Object... objects) {
        this();
        format = StringUtils.replace(format, "{}", "%s");
        this.message = String.format(format, objects);
    }

    public BusinessException(String msg, Throwable cause, Object... objects) {
        this();
        String format = StringUtils.replace(msg, "{}", "%s");
        this.message = String.format(format, objects);
    }

    public BusinessException(ResultEnum resultEnum, Object data) {
        this(resultEnum);
        this.data = data;
    }

    public BusinessException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
        this.code = resultEnum.code();
        this.message = resultEnum.message();
    }

    public int getCode() {
        return code;
    }

    public BusinessException setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BusinessException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 需要将自定义的message覆盖ResultEnum的默认message
     */
    public ResultEnum getResultEnum() {
        return null != resultEnum ? resultEnum.setMessage(message) : null;
    }

    public BusinessException setResultEnum(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
        return this;
    }

    public Object getData() {
        return data;
    }

    public BusinessException setData(Object data) {
        this.data = data;
        return this;
    }
}