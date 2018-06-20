package com.whxm.harbor.service.impl;

import com.whxm.harbor.mapper.VisitCountLogMapper;
import com.whxm.harbor.service.VisitLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class VisitLogServiceImpl implements VisitLogService {

    @Resource
    private VisitCountLogMapper logger;

    @Override
    public int recordShopVisit(String ip, String terminalId) {
        logger.logShopVisit();

        return 0;
    }

    @Override
    public int recordTerminalVisit(String ip, String shopId) {

        logger.logTerminalVisit();
        return 0;
    }
}
