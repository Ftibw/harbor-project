package com.whxm.harbor.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class VisitAspect {

    @Around("@within(com.whxm.harbor.annotation.VisitLogger)||@annotation(com.whxm.harbor.annotation.VisitLogger)")
    public Object visitLogPoint(ProceedingJoinPoint pjp){

        return null;
    }
}
