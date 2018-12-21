package com.whxm.harbor.wechat;

import java.util.HashMap;
import java.util.Map;

public enum PushEnum {

    FIRST("first"),
    KEYWORD1("key1"),
    KEYWORD2("key2"),
    KEYWORD3("key3"),
    REMARK("Remark");

    public String key;

    public Map<Object, Object> values = new HashMap<>(2);

    PushEnum(String key) {
        this.key = key;
    }
}
