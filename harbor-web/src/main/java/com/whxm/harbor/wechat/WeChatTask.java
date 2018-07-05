package com.whxm.harbor.wechat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whxm.harbor.conf.PathConfig;
import com.whxm.harbor.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class WeChatTask {

    private final Logger logger = LoggerFactory.getLogger(WeChatTask.class);

    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private PathConfig pathConfig;

    @Scheduled(initialDelay = 1000, fixedRate = 7000000)
    public void accessToken() {
        RestTemplate client = new RestTemplate();

        client.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //-----------------------------------access_token---------------------------------------

        String accessTokenUrl = String.format(WeChatConstant.ACCESS_TOKEN_URL_FORMAT_2,
                weChatConfig.getAppId(), weChatConfig.getSecret());

        Map ret = client.getForObject(accessTokenUrl, Map.class);

        String accessToken = String.valueOf(ret.get("access_token"));

        weChatConfig.setAccessToken(accessToken);

        //----------------------------------------openid---------------------------------------

        String userListUrl = String.format(WeChatConstant.FANS_URL_FORMAT_2, accessToken, "");

        String ret1 = client.getForObject(userListUrl, String.class);

        WeChatUser weChatUser = JacksonUtils.readGenericTypeValue(ret1, new TypeReference<WeChatUser<UserIdList>>() {
        });

        if (null != weChatUser) {
            UserIdList data = weChatUser.getData();
            weChatConfig.setOpenIds(null == data ? null : data.getOpenIds());
        }

        //------------------------------------template_id-------------------------------------

        String templateListUrl = String.format(WeChatConstant.TEMPLATE_URL_FORMAT_1, accessToken);

        String ret2 = client.getForObject(templateListUrl, String.class);

        Map<String, List<WeChatTemplate>> stringListMap = JacksonUtils.readGenericTypeValue(ret2, new TypeReference<Map<String, List<WeChatTemplate>>>() {
        });

        if (null != stringListMap) {
            List<WeChatTemplate> templateList = stringListMap.get(WeChatConstant.TEMPLATE_LIST_KEY);
            weChatConfig.setTemplates(templateList);
        }
    }

    public void pushException(Exception e) {

        RestTemplate client = new RestTemplate();

        String bugTemplateId = null;

        //中文乱码会导致无法成功匹配title,所以获取配置的时候一定要设置编码
        for (WeChatTemplate template : weChatConfig.getTemplates()) {
            if ("后台异常反馈".equals(template.getTitle())) {
                bugTemplateId = template.getTemplateId();
            }
        }
        PushBean pushBean = PushBean.getDefaultBean().setTemplateId(bugTemplateId);

        weChatConfig.getOpenIds().forEach(openid -> {

            String url = String.format(WeChatConstant.PUSH_URL_FORMAT_1, weChatConfig.getAccessToken());

            pushBean.setToUser(openid)
                    .setUrl(pathConfig.getResourcePath() + pathConfig.getLogUri())
                    .setValue(BugEnum.FIRST, "抛异常了，哗了狗")
                    .setValue(BugEnum.EXCEPTION_TYPE, e.getClass().getName())
                    .setValue(BugEnum.EXCEPTION_MESSAGE, e.getLocalizedMessage())
                    .setValue(BugEnum.REMARK, Arrays.toString(e.getStackTrace()));

            String ret = client.postForObject(url, pushBean, String.class);

            logger.info("给用户[{}]推送的异常反馈,响应结果为[{}]", openid, ret);
        });
    }
}
