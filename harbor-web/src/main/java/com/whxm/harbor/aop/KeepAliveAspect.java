package com.whxm.harbor.aop;

import com.whxm.harbor.service.TerminalService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class KeepAliveAspect {

    private final Logger logger = LoggerFactory.getLogger(KeepAliveAspect.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private TerminalService terminalService;

    @Around("@within(com.whxm.harbor.annotation.KeepAliveDetect)||@annotation(com.whxm.harbor.annotation.KeepAliveDetect)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        String terminalNumber = String.valueOf(joinPoint.getArgs()[0]);

        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        //String.valueOf(System.currentTimeMillis())
        redisTemplate.boundHashOps("terminalsStatus").put(terminalNumber, System.currentTimeMillis());

        terminalService.updateTerminalOnline(terminalNumber);

        return joinPoint.proceed();
    }


    @Scheduled(initialDelay = 10, fixedRate = 10000)
    public void keepAliveDetect() {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps("terminalsStatus");
        Map<Object, Object> map = hashOps.entries();

        List<Object> list = new ArrayList<>();

        map.forEach((terminalNumber, lastTimePoint) -> {
            if (System.currentTimeMillis() > (Long.parseLong(String.valueOf(lastTimePoint)) + 10000)) {
                list.add(String.valueOf(terminalNumber));
            }
        });


        if (!list.isEmpty()) {
            terminalService.updateTerminalOffline(list);
            hashOps.delete(list.toArray());
            logger.info("编号为{}的终端离线", list);
        }
    }
}
