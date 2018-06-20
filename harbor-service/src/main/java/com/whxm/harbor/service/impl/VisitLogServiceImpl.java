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
    public int recordVisit(String param, String ip, String signature) {

        int affectRow = 0;

        if (signature.toLowerCase().contains("shop")) {

            affectRow = logger.logShopVisit(param, ip, signature);

        } else if (signature.toLowerCase().contains("terminal")) {

            affectRow = logger.logTerminalVisit(param, ip, signature);
        }

        return affectRow;
    }
}
