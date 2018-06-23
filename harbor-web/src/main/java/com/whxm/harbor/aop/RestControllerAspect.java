package com.whxm.harbor.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.handler.GlobalExceptionHandler;
import com.whxm.harbor.utils.IPv4Utils;
import com.whxm.harbor.utils.JacksonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhumaer
 * @desc 请求参数、响应体统一日志打印
 * @since 10/10/2017 9:54 AM
 */
@Aspect
@Component
public class RestControllerAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 环绕通知
     *
     * @param joinPoint 连接点
     * @return 切入点返回值
     * @throws Throwable 异常信息
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) " +
            "|| @annotation(org.springframework.web.bind.annotation.RestController)")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        boolean logFlag = this.needToLog(method);
        if (!logFlag) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //登录拦截器会将登录用户信息存入request,getUserInfoFromRequest(request)

        String ip = IPv4Utils.getIpAddress(request);
        String methodName = this.getMethodName(joinPoint);
        String params = this.getParamsJson(joinPoint);
        String requester = "unknown";
        String userAgent = request.getHeader("user-agent");

        logger.info("Started request requester [{}] method [{}] params [{}] IP [{}] userAgent [{}]", requester, methodName, params, ip, userAgent);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        logger.info("Ended request requester [{}] method [{}] params[{}] response is [{}] cost [{}] millis ",
                requester, methodName, params, result, System.currentTimeMillis() - start);
        return result;
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String SHORT_METHOD_NAME_SUFFIX = "(..)";
        if (methodName.endsWith(SHORT_METHOD_NAME_SUFFIX)) {
            methodName = methodName.substring(0, methodName.length() - SHORT_METHOD_NAME_SUFFIX.length());
        }
        return methodName;
    }

    private String getParamsJson(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            String paramStr;
            if (arg instanceof HttpServletResponse) {
                paramStr = HttpServletResponse.class.getSimpleName();
            } else if (arg instanceof HttpServletRequest) {
                paramStr = HttpServletRequest.class.getSimpleName();
            } else if (arg instanceof MultipartFile) {
                long size = ((MultipartFile) arg).getSize();
                paramStr = MultipartFile.class.getSimpleName() + " size:" + size;
            } else {
                paramStr = this.deleteSensitiveContent(arg);//JacksonUtils.toJson直接转json变成了过滤敏感词
            }
            sb.append(paramStr).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 判断是否需要记录日志
     */
    private boolean needToLog(Method method) {
        return method.getAnnotation(GetMapping.class) == null
                && !method.getDeclaringClass().equals(GlobalExceptionHandler.class);
    }

    /**
     * 删除参数中的敏感内容
     *
     * @param obj 参数对象
     * @return 去除敏感内容后的参数对象
     */
    @SuppressWarnings("unchecked")
    private String deleteSensitiveContent(Object obj) {

        if (obj == null) {
            return null;
        }

        if (obj instanceof Exception) {
            return obj.getClass().getName();
        }

        Map map = null;
        try {
            String params = JacksonUtils.toJson(obj);
            //排除基本类型,包装类型,String等之后再进行json反序列化
            if (null != params
                    && params.matches("^\\{.*:.*}$")) {

                map = JacksonUtils.readValue(params, Map.class);

                List<String> sensitiveFieldList = this.getSensitiveFieldList();

                if (null != map)
                    for (String sensitiveField : sensitiveFieldList) {
                        if (map.containsKey(sensitiveField)) {
                            map.put(sensitiveField, "******");
                        }
                    }
            } else {
                //基本类型,包装类型,String等简单参数直接返回
                return params;
            }
        } catch (ClassCastException e) {
            return String.valueOf(obj);
        }
        return JacksonUtils.toJson(map);
    }

    /**
     * 敏感字段列表（当然这里你可以更改为可配置的）
     */
    private List<String> getSensitiveFieldList() {
        List<String> sensitiveFieldList = Lists.newArrayList();
        sensitiveFieldList.add("pwd");
        sensitiveFieldList.add("password");
        sensitiveFieldList.add("userPassword");
        return sensitiveFieldList;
    }
}