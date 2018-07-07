package com.whxm.harbor.constant;

public class Constant {

    public static final int YES = 1;

    public static final int NO = 0;

    public static final String DEFAULT_FILTER_METHOD = "POST";

    public static final String DEFAULT_FILTER_CONTENT_TYPE = "application/json";

    //表单提交防重时长15秒(15000毫秒)
    public static final int DEFAULT_SUBMIT_EXPIRE_TIME = 15000;

    //终端keep alive时间间隔5分钟(300000毫秒)
    public static final long KEEP_ALIVE_INTERVAL = 300000;
    //状态状态存储key
    public static final String TERMINAL_STATUS_KEY = "TERMINAL_STATUS_KEY";

    public static final long TASK_INIT_DELAY = 1000;

    public static final int NEGATIVE_TIME_POINT_HOLDER = 0;

    public static final String DEFAULT_FILE_SEPARATOR = "/";
}
