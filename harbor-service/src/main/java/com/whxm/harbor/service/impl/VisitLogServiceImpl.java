package com.whxm.harbor.service.impl;

import com.whxm.harbor.mapper.VisitCountLogMapper;
import com.whxm.harbor.service.VisitLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;

@Service
@Transactional
public class VisitLogServiceImpl implements VisitLogService {

    @Resource
    private VisitCountLogMapper logger;

    @Override
    public int recordVisit(String number, String ip, String message) {

        return logger.writeLog(number, ip, message);
    }
}
