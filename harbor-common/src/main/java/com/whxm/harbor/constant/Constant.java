package com.whxm.harbor.constant;

public class Constant {

    public static final int YES = 1;

    public static final int NO = 0;

    public static final String DEFAULT_FILTER_METHOD = "POST";

    public static final String DEFAULT_FILTER_CONTENT_TYPE = "application/json";

    //表单提交防重时长15秒(15000毫秒)
    public static final int DEFAULT_SUBMIT_EXPIRE_TIME = 15000;

    /**
     * windows中
     * "C:\\Users\\ftibw\\Desktop\\dev\\install\\tomcat\\apache-tomcat-8.0.45\\apache-tomcat-8.0.45\\webapps\\resources"
     */
    public static final String RESOURCE_ABSOLUTE_DIRECTORY_PATH = "/usr/local/tomcat/webapps/resources";

    public static final String PICTURE_UPLOAD_ROOT_DIRECTORY = "picture";
    //终端keep alive时间间隔5分钟(300000毫秒)
    public static final long KEEP_ALIVE_INTERVAL = 300000;
}
