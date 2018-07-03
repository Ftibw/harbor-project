package com.whxm.harbor.interceptor;

import com.whxm.harbor.bean.User;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.lock.RedisDistributedLock;
import com.whxm.harbor.utils.JacksonUtils;
import com.whxm.harbor.utils.MD5Utils;
import com.whxm.harbor.utils.RequestJsonUtils;
import com.whxm.harbor.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisDistributedLock lock;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter("token");

        if (token != null && 64 == token.length()) {

            //从redis获取user信息
            String userId = TokenUtils.order(token);

            String salt = TokenUtils.salt(token);

            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(userId);

            Long lastTimePoint = (Long) hashOps.get(salt);

            if (null != lastTimePoint &&
                    System.currentTimeMillis() < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) + lastTimePoint) {
                //instanceof LinkedHashMap
                Object useInfo = hashOps.get(userId);

                request.setAttribute(Constant.REQUEST_USER_KEY, JacksonUtils.readValue(JacksonUtils.toJson(useInfo), User.class));

                //不幂等的操作如下(均为POST)
                /*terminal/bizTerminal
                floor/bizFloor
                screensaver/bizScreensaver
                bizFormat/bizFormat
                activityMaterial/bizActivityMaterial
                activity/bizActivity*/

                //防止表单重复提交,主要是防止不幂等的新增请求
                //只是为了防止数据重复的请求,而不是对数据进行逻辑过滤
                if (Constant.DEFAULT_FILTER_METHOD.equals(request.getMethod().toUpperCase())
                        && Objects.nonNull(request.getContentType())
                        && request.getContentType().toLowerCase().contains(Constant.DEFAULT_FILTER_CONTENT_TYPE)) {

                    String uri = request.getRequestURI();

                    String params = RequestJsonUtils.getRequestPostStr(request);

                    String lockKey = uri + userId + MD5Utils.MD5(params);

                    if (lock.lock(lockKey, token, Constant.DEFAULT_SUBMIT_EXPIRE_TIME)) return true;

                    else {
                        response.setContentType("text/html;charset=utf-8");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().println("表单重复提交了");
                        // 400 状态码 用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        return false;
                    }

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

    /**
     * 整个业务流程完成后,释放redis分布式锁
     */
    /*@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String token = request.getParameter("token");

        String userId = TokenUtils.order(token);

        String uri = request.getRequestURI();

        String params = RequestJsonUtils.getRequestPostStr(request);

        String lockKey = uri + userId + MD5Utils.MD5(params);

        //System.out.println(lockKey);

        lock.unlock(lockKey, token);    //true清除成功/false清除失败
    }*/
}