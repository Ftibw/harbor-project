package com.whxm.harbor.bean;

import com.whxm.harbor.enums.ResultEnum;


public class Result {

    private Integer code;

    private String msg;

    private Object data;

    public Result() {
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success() {
        Result result = new Result();
        result.setResultEnum(ResultEnum.SUCCESS);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setResultEnum(ResultEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result success(ResultEnum resultEnum) {
        Result result = new Result();
        result.setResultEnum(resultEnum);
        return result;
    }

    public static Result failure(ResultEnum resultEnum) {
        Result result = new Result();
        result.setResultEnum(resultEnum);
        return result;
    }

    public static Result failure(ResultEnum resultEnum, Object data) {
        Result result = new Result();
        result.setResultEnum(resultEnum);
        result.setData(data);
        return result;
    }

    //=====================================================================

    public void setResultEnum(ResultEnum code) {
        this.code = code.code();
        this.msg = code.message();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}