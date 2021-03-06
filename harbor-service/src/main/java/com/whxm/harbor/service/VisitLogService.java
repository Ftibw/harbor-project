package com.whxm.harbor.service;

/**
 * 访问统计日志记录器
 */
public interface VisitLogService {

    /**
     * 记录访问统计日志  终端id/终端ip/商铺id/访问时间
     *
     * @param ip      访问请求的IP
     * @param message 日志信息
     * @return 日志记录条数
     */
    int recordVisit(String number,String ip, String message);

}
