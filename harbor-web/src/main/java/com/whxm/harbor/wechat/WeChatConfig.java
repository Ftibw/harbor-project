package com.whxm.harbor.wechat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class WeChatConfig {

    private String accessToken;
    @Value("${wechat.app-id}")
    private String appId;
    @Value("${wechat.secret}")
    private String secret;

    private List<String> openIds;

    private List<WeChatTemplate> templates;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<String> getOpenIds() {
        return openIds;
    }

    public void setOpenIds(List<String> openIds) {
        this.openIds = openIds;
    }

    public List<WeChatTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<WeChatTemplate> templates) {
        this.templates = templates;
    }
}
