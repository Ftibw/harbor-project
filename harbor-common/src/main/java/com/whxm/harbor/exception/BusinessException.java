package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ExceptionEnum;
import com.whxm.harbor.enums.ResultEnum;
import org.apache.commons.lang3.StringUtils;


/**
 * @author zhumaer
 * @desc 业务异常类
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
        this.resultEnum.setMessage(message);
        return this;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
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