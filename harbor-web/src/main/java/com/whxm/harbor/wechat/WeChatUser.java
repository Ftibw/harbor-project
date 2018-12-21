package com.whxm.harbor.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeChatUser<T extends UserIdList> {

    private Long total;

    private Long count;

    private T data;

    @JsonProperty("next_openid")
    private String nextOpenId;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getNextOpenId() {
        return nextOpenId;
    }

    public void setNextOpenId(String nextOpenId) {
        this.nextOpenId = nextOpenId;
    }
}


