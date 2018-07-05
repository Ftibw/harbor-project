package com.whxm.harbor.wechat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whxm.harbor.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class WeChatTask {

    @Autowired
    private WeChatConfig weChatConfig;

    @Scheduled(initialDelay = 1000, fixedRate = 7000000)
    public void accessToken() {
        RestTemplate template = new RestTemplate();

        //----------------------------------------------------------------------------

        String accessTokenUrl = String.format(WeChatConstant.ACCESS_TOKEN_URL_FORMAT_2,
                weChatConfig.getAppId(), weChatConfig.getSecret());

        Map ret = template.getForObject(accessTokenUrl, Map.class);

        String accessToken = String.valueOf(ret.get("access_token"));

        weChatConfig.setAccessToken(accessToken);

        //----------------------------------------------------------------------------

        String userListUrl = String.format(WeChatConstant.FANS_URL_FORMAT_2, accessToken, "");

        String ret1 = template.getForObject(userListUrl, String.class);

        WeChatUser weChatUser = JacksonUtils.readGenericTypeValue(ret1, new TypeReference<WeChatUser<UserIdList>>() {
        });

        if (null != weChatUser) {
            UserIdList data = weChatUser.getData();
            weChatConfig.setOpenIds(null == data ? null : data.getOpenIds());
        }

        //----------------------------------------------------------------------------

        String templateListUrl = String.format(WeChatConstant.TEMPLATE_URL_FORMAT_1, accessToken);

        String ret2 = template.getForObject(templateListUrl, String.class);

        Map<String, List<WeChatTemplate>> stringListMap = JacksonUtils.readGenericTypeValue(ret2, new TypeReference<Map<String, List<WeChatTemplate>>>() {
        });

        if (null != stringListMap) {
            List<WeChatTemplate> templateList = stringListMap.get(WeChatConstant.TEMPLATE_LIST_KEY);
            weChatConfig.setTemplates(templateList);
        }
    }
}
