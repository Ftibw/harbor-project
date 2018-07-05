package com.whxm.harbor.wechat;

public class WeChatConstant {

    public static final String ACCESS_TOKEN_URL_FORMAT_2 = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    public static final String PUSH_URL_FORMAT_1 = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
    //next_openid设置起始点,值为空时,默认从第一个用户开始
    public static final String FANS_URL_FORMAT_2 = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s&next_openid=%s";

    public static final String TEMPLATE_URL_FORMAT_1 = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=%s";

    public static final String TEMPLATE_LIST_KEY = "template_list";

    public static final String DEFAULT_FONT_COLOR = "#173177";

}
