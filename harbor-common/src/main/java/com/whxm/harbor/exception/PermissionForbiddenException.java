package com.whxm.harbor.exception;


import com.whxm.harbor.enums.ResultEnum;

public class PermissionForbiddenException extends BusinessException {

    public PermissionForbiddenException() {
        super();
    }

    public PermissionForbiddenException(Object data) {
        super();
        super.data = data;
    }

    public PermissionForbiddenException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public PermissionForbiddenException(ResultEnum resultEnum, Object data) {
        super(resultEnum, data);
    }

    public PermissionForbiddenException(String msg) {
        super(msg);
    }

    public PermissionForbiddenException(String formatMsg, Object... objects) {
        super(formatMsg, objects);
    }
}
