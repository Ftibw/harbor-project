package com.whxm.harbor.utils;

import com.whxm.harbor.exception.ParameterInvalidException;
import org.springframework.util.StringUtils;

public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
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
