package com.whxm.harbor.aop;

import com.whxm.harbor.lock.RedisDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KeepAliveAspect {

    private final Logger logger = LoggerFactory.getLogger(KeepAliveAspect.class);

    @Autowired
    private RedisDistributedLock lock;

    //signature.toLongString()  = public com.whxm.harbor.bean.Result com.whxm.harbor.controller.ShopController.updateShopVisit(java.lang.String)
    //signature.toShortString() =   ShopController.updateShopVisit(..)
    //signature.toString()  = Result com.whxm.harbor.controller.ShopController.updateShopVisit(String)
    //Class clazz = joinPoint.getTarget().getClass();
    //map.put("class", clazz.toString());
    //Method method = ((MethodSignature)signature ).getMethod();
    //String paramName = method.getParameters()[0].getName();

    @Around("@within(com.whxm.harbor.annotation.KeepAliveDetect)||@annotation(com.whxm.harbor.annotation.KeepAliveDetect)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();

        return joinPoint.proceed();
    }
}
