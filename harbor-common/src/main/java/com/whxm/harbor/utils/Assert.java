package com.whxm.harbor.utils;

import com.whxm.harbor.exception.ParameterInvalidException;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/26 04:30
 */
public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ParameterInvalidException(message);
        }
    }

    public static void notEmpty(Object object, String message) {
        if (object == null) {
            throw new ParameterInvalidException(message);
        }
        if (object instanceof List&&((List)object).isEmpty()){
            throw new ParameterInvalidException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new ParameterInvalidException(message);
        }
    }

    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new ParameterInvalidException(message);
        }
    }
}
