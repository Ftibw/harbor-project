package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ExceptionEnum;
import com.whxm.harbor.enums.ResultEnum;
import org.apache.commons.lang3.StringUtils;



/**
 * @desc 业务异常类
 * 
 * @author zhumaer
 * @since 9/18/2017 3:00 PM
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 194906846739586856L;

    protected String code;

    protected String message;

    protected ResultEnum resultEnum;

    protected Object data;

    public BusinessException() {
        ExceptionEnum exceptionEnum = ExceptionEnum.getByEClass(this.getClass());
        if (exceptionEnum != null) {
            resultEnum = exceptionEnum.getResultEnum();
            code = exceptionEnum.getResultEnum().code().toString();
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
        this.message= String.format(format, objects);
    }

    public BusinessException(ResultEnum resultEnum, Object data) {
        this(resultEnum);
        this.data = data;
    }

    public BusinessException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
        this.code = resultEnum.code().toString();
        this.message = resultEnum.message();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}