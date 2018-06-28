package com.whxm.harbor.aop;

import com.whxm.harbor.annotation.KeepAliveDetect;
import com.whxm.harbor.lock.RedisDistributedLock;
import com.whxm.harbor.service.TerminalService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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

        redisTemplate.boundValueOps(terminalNumber).set(String.valueOf(System.currentTimeMillis()));



        terminalService.updateTerminalOnline(terminalNumber);

        return joinPoint.proceed();
    }

}
