package com.whxm.harbor.aop;

import com.whxm.harbor.service.VisitLogService;
import com.whxm.harbor.utils.IPv4Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class VisitAspect {

    private final Logger logger = LoggerFactory.getLogger(VisitAspect.class);

    @Autowired
    private VisitLogService visitLogService;

    @Around("@within(com.whxm.harbor.annotation.VisitLogger)||@annotation(com.whxm.harbor.annotation.VisitLogger)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        String signature = joinPoint.getSignature().getName();

        Object param = joinPoint.getArgs()[0];

        if (param instanceof String) {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String ip = IPv4Util.getIpAddress(request);

            int affectRow = visitLogService.recordVisit(param.toString(), ip, signature + "(" + param + ")");

            if (1 == affectRow) {
                logger.info("调用方法[{}({})],日志记录成功", signature, param);
            }
        }

        return joinPoint.proceed();
    }
}
