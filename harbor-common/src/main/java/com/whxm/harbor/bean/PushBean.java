package com.whxm.harbor.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.whxm.harbor.constant.WeChatConstant;
import com.whxm.harbor.enums.BugEnum;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class PushBean {

    @JsonProperty("touser")
    private String toUser;

    @JsonProperty("template_id")
    private String templateId;

    private String url;

    private Map<Object, Object> data = new HashMap<>(4);

    private static final String COLOR_KEY = "color";

    private static final String VALUE_KEY = "value";

    public String getToUser() {
        return toUser;
    }

    public PushBean setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getTemplateId() {
        return templateId;
    }

    public PushBean setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PushBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<Object, Object> getData() {
        return data;
    }

    public PushBean setData(Map<Object, Object> data) {
        this.data = data;
        return this;
    }

    public PushBean setValue(BugEnum field, String value) {

        field.values.put(VALUE_KEY, value);

        this.data.putIfAbsent(field.key, field.values);

        return this;
    }

    public PushBean setColor(BugEnum field, String color) {

        field.values.put(COLOR_KEY, color);

        this.data.putIfAbsent(field.key, field.values);

        return this;
    }

    public static PushBean getDefaultBean() {

        return new PushBean()
                .setToUser(WeChatConstant.MY_OPEN_ID)
                .setTemplateId(WeChatConstant.MY_BUG_TEMPLATE_ID)
                .setColor(BugEnum.FIRST, WeChatConstant.DEFAULT_FONT_COLOR)
                .setColor(BugEnum.EXCEPTION_TYPE, WeChatConstant.DEFAULT_FONT_COLOR)
                .setColor(BugEnum.EXCEPTION_MESSAGE, WeChatConstant.DEFAULT_FONT_COLOR)
                .setColor(BugEnum.REMARK, WeChatConstant.DEFAULT_FONT_COLOR);
    }


    public static void main(String[] args) {
        //2小时获取一次
        String accessToken = "11_bSD4qSSa4fDnnw5zWfj4UzaRH4a7yihRMcUMHzcOfAE7BRj4I_dmKIROG5hwnN5Bqx_pEB7c6oLv3qgdh0so9XJ1UdJuVXSrxfsMQAciwQ-yLwtYxiPW_HXcSTMV_cREbxRoAtlHW1pLAV_iKBNeADAFWI";

        PushBean bean = PushBean.getDefaultBean()
                .setUrl("www.douyu.com")
                .setValue(BugEnum.FIRST, "unknown")
                .setValue(BugEnum.EXCEPTION_TYPE, "sqlException")
                .setValue(BugEnum.EXCEPTION_MESSAGE, "com.xxx.bean.User")
                .setValue(BugEnum.REMARK, "login(User user)");

        String url = String.format(WeChatConstant.WE_CHAT_PUSH_URL_FORMAT, accessToken);

        String ret = new RestTemplate().postForObject(url, bean, String.class);

        System.out.println(ret);
    }
}
