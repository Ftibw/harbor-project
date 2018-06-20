package com.whxm.harbor.aop;

import com.whxm.harbor.service.VisitLogService;
import com.whxm.harbor.utils.IPv4Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class VisitAspect {

    @Autowired
    private VisitLogService visitLogService;

    @Around("@within(com.whxm.harbor.annotation.VisitLogger)||@annotation(com.whxm.harbor.annotation.VisitLogger)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) {

        String signature = joinPoint.getSignature().getName();

        String param = joinPoint.getArgs()[0].toString();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String ip = IPv4Util.getIpAddress(request);

        visitLogService.recordVisit(ip,param,signature);

        return null;
    }
}
