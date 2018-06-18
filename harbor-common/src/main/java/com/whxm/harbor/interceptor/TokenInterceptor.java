package com.whxm.harbor.interceptor;

import com.whxm.harbor.utils.RequestJsonUtils;
import com.whxm.harbor.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter("token");

        if (token != null && 64 == token.length()) {
            //分布式的时候,设置String序列化器
            StringRedisSerializer serializer = new StringRedisSerializer();

            redisTemplate.setKeySerializer(serializer);

            redisTemplate.setValueSerializer(serializer);

            //从redis获取user信息
            String userId = TokenUtils.order(token);
            String salt = TokenUtils.salt(token);
            if (null != userId
                    && null != salt
                    && salt.equals(redisTemplate.boundValueOps(userId).get())) {

                //防止表单重复提交,主要是防止不幂等的新增请求
                if ("POST".equals(request.getMethod().toUpperCase())
                        ) {
                    //  terminal/bizTerminal
                    //  floor/bizFloor
                    //  screensaver/bizScreensaver
                    //  bizFormat/bizFormat
                    //  activityMaterial/bizActivityMaterial
                    //  activity/bizActivity
                    String params = RequestJsonUtils.getRequestPostStr(request);

                    String uri = request.getRequestURI();

                    System.out.println(uri + "\n" + userId + "\n"+params);
                }


                return true;
            }
        }

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("未授权");
        // 401 状态码 没有权限访问
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        return false;
    }
}