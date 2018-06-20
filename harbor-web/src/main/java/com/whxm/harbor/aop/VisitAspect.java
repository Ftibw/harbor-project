package com.whxm.harbor.aop;

import com.alibaba.druid.support.json.JSONUtils;
import com.whxm.harbor.bean.Result;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
public class VisitAspect {

    private final Logger logger = LoggerFactory.getLogger(VisitAspect.class);

    @Autowired
    private VisitLogService visitLogService;

    //signature.toLongString()  = public com.whxm.harbor.bean.Result com.whxm.harbor.controller.ShopController.updateShopVisit(java.lang.String)
    //signature.toShortString() =   ShopController.updateShopVisit(..)
    //signature.toString()  = Result com.whxm.harbor.controller.ShopController.updateShopVisit(String)
    //Class clazz = joinPoint.getTarget().getClass();
    //map.put("class", clazz.toString());
    //Method method = ((MethodSignature)signature ).getMethod();
    //String paramName = method.getParameters()[0].getName();

    @Around("@within(com.whxm.harbor.annotation.VisitLogger)||@annotation(com.whxm.harbor.annotation.VisitLogger)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        Signature signature = joinPoint.getSignature();

        Object param = joinPoint.getArgs()[0];

        if (param instanceof String) {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String ip = IPv4Util.getIpAddress(request);

            Object result = joinPoint.proceed();

            if (Objects.nonNull(result)
                    && result instanceof Result
                    && ((Result) result).getStatus() == HttpStatus.OK.value()) {

                if (this.logger.isDebugEnabled()) {
                    logger.info("调用方法[{}({})]成功", signature, param);
                }

                //应该写到自定义缓存或中间件缓存中,定量/定时批处理,异步批处理提高读写和响应速度
                Map<String, String> map = new HashMap<>();

                map.put("method", signature.toShortString()
                        .replaceAll("^(.*)\\(..\\)$", "$1"));

                map.put("param", param.toString());

                visitLogService.recordVisit(ip, JSONUtils.toJSONString(map));

            }

            return result;
        }

        return joinPoint.proceed();
    }
}
