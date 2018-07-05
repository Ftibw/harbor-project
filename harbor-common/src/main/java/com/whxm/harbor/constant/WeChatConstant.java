package com.whxm.harbor.constant;

public class WeChatConstant {


    public static final String ACCESS_TOKEN_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    public static final String WE_CHAT_PUSH_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    public static final String DEFAULT_FONT_COLOR = "#173177";

    //下面需要改为可配置的
    public static final String MY_APP_ID = "wx64a4ab9041d6344b";

    public static final String MY_SECRET = "78ce2dae8e9b51ac1f26c6eda6bc079f";

    public static final String MY_OPEN_ID = "oAGvi1K-d5mNpIMssitPAk5Zsw0E";

    public static final String MY_BUG_TEMPLATE_ID = "jYoWogKoXrkxmYWrc8Nv02nO3pVdO2hjoc4K8s5p1qo";
}
