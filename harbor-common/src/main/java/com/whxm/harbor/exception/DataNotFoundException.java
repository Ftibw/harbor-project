package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ResultEnum;

public class DataNotFoundException extends BusinessException {

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(Object data) {
        super();
        super.data = data;
    }

    public DataNotFoundException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public DataNotFoundException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public DataNotFoundException(String msg) {
        super(msg);
    }

    public DataNotFoundException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
