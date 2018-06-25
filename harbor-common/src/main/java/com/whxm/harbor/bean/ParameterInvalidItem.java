package com.whxm.harbor.bean;

public class ParameterInvalidItem {

    private Object fieldName;

    private Object message;

    public void setFieldName(Object fieldName) {
        this.fieldName = fieldName;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getFieldName() {
        return fieldName;
    }

    public Object getMessage() {
        return message;
    }
}
