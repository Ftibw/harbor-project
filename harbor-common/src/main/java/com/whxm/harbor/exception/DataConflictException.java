package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ResultEnum;

public class DataConflictException extends BusinessException {

    public DataConflictException() {
        super();
    }

    public DataConflictException(Object data) {
        super();
        super.data = data;
    }

    public DataConflictException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public DataConflictException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public DataConflictException(String msg) {
        super(msg);
    }

    public DataConflictException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
