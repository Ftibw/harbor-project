package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ResultEnum;

/**
 * @author zhumaer
 * @desc 参数无效异常
 * @since 9/18/2017 3:00 PM
 */
public class ParameterInvalidException extends BusinessException {

    public ParameterInvalidException() {
        super();
    }

    public ParameterInvalidException(Object data) {
        super();
        super.data = data;
    }

    public ParameterInvalidException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public ParameterInvalidException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public ParameterInvalidException(String msg) {
        super(msg);
    }

    public ParameterInvalidException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}