package com.whxm.harbor.exception;

import com.whxm.harbor.enums.ResultEnum;

public class UserNotLoginException extends BusinessException {
    public UserNotLoginException() {
        super();
    }

    public UserNotLoginException(Object data) {
        super();
        super.data = data;
    }

    public UserNotLoginException(ResultEnum resultCode) {
        super(resultCode);
    }

    public UserNotLoginException(ResultEnum resultCode, Object data) {
        super(resultCode, data);
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

    public UserNotLoginException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }

}
