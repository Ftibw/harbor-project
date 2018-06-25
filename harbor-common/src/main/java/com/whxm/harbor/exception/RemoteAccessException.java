package com.whxm.harbor.exception;


import com.whxm.harbor.enums.ResultEnum;

public class RemoteAccessException extends BusinessException {

    public RemoteAccessException() {
        super();
    }

    public RemoteAccessException(Object data) {
        super();
        super.data = data;
    }

    public RemoteAccessException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public RemoteAccessException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public RemoteAccessException(String msg) {
        super(msg);
    }

    public RemoteAccessException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
