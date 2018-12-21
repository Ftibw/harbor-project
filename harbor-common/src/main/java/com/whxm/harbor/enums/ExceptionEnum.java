package com.whxm.harbor.enums;

import com.whxm.harbor.exception.*;
import org.springframework.http.HttpStatus;

public enum ExceptionEnum {

    /**
     * 无效参数
     */
    PARAMETER_INVALID(ParameterInvalidException.class, HttpStatus.OK/*BAD_REQUEST*/, ResultEnum.PARAM_IS_INVALID),

    /**
     * 数据未找到
     */
    NOT_FOUND(DataNotFoundException.class, HttpStatus.OK/*NOT_FOUND*/, ResultEnum.RESULT_DATA_NONE),

    /**
     * 数据已存在
     */
    CONFLICT(DataConflictException.class, HttpStatus.OK/*CONFLICT*/, ResultEnum.DATA_ALREADY_EXISTED),

    /**
     * 用户未登录
     */
    UNAUTHORIZED(UserNotLoginException.class, HttpStatus.OK/*UNAUTHORIZED*/, ResultEnum.USER_NOT_LOGGED_IN),

    /**
     * 无访问权限
     */
    FORBIDDEN(PermissionForbiddenException.class, HttpStatus.OK/*FORBIDDEN*/, ResultEnum.PERMISSION_NO_ACCESS),

    /**
     * 远程访问时错误
     */
    REMOTE_ACCESS_ERROR(RemoteAccessException.class, HttpStatus.OK/*INTERNAL_SERVER_ERROR*/, ResultEnum.INTERFACE_OUTER_INVOKE_ERROR),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(InternalServerException.class, HttpStatus.INTERNAL_SERVER_ERROR, ResultEnum.SYSTEM_INNER_ERROR);

    private Class<? extends BusinessException> eClass;

    private HttpStatus httpStatus;

    private ResultEnum resultEnum;

    ExceptionEnum(Class<? extends BusinessException> eClass, HttpStatus httpStatus, ResultEnum resultEnum) {
        this.eClass = eClass;
        this.httpStatus = httpStatus;
        this.resultEnum = resultEnum;
    }

    public Class<? extends BusinessException> getEClass() {
        return eClass;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }

    public static boolean isSupportHttpStatus(int httpStatus) {
        for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
            if (exceptionEnum.httpStatus.value() == httpStatus) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSupportException(Class<?> z) {
        for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
            if (exceptionEnum.eClass.equals(z)) {
                return true;
            }
        }

        return false;
    }

    public static ExceptionEnum getByHttpStatus(HttpStatus httpStatus) {
        if (httpStatus == null) {
            return null;
        }

        for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
            if (httpStatus.equals(exceptionEnum.httpStatus)) {
                return exceptionEnum;
            }
        }

        return null;
    }

    public static ExceptionEnum getByEClass(Class<? extends BusinessException> eClass) {
        if (eClass == null) {
            return null;
        }

        for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
            if (eClass.equals(exceptionEnum.eClass)) {
                return exceptionEnum;
            }
        }

        return null;
    }
}
