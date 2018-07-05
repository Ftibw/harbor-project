package com.whxm.harbor.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserIdList {

    @JsonProperty("openid")
    private List<String> openIds;

    public List<String> getOpenIds() {
        return openIds;
    }

    public void setOpenIds(List<String> openIds) {
        this.openIds = openIds;
    }
}
