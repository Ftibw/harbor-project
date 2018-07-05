package com.whxm.harbor.wechat;

import java.util.HashMap;
import java.util.Map;

public enum BugEnum {

    FIRST("first"),
    EXCEPTION_TYPE("exceptionType"),
    EXCEPTION_MESSAGE("exceptionMessage"),
    REMARK("Remark");

    public String key;

    public Map<Object, Object> values = new HashMap<>(2);

    BugEnum(String key) {
        this.key = key;
    }
}
