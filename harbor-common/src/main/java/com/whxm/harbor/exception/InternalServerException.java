package com.whxm.harbor.exception;


import com.whxm.harbor.enums.ResultEnum;

public class InternalServerException extends BusinessException {

    public InternalServerException() {
        super();
    }

    public InternalServerException(Object data) {
        super();
        super.data = data;
    }

    public InternalServerException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public InternalServerException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public InternalServerException(String msg) {
        super(msg);
    }

    public InternalServerException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
