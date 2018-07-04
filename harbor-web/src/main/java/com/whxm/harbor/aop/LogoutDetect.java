package com.whxm.harbor.aop;

import com.whxm.harbor.constant.Constant;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class LogoutDetect {

    private final Logger logger = LoggerFactory.getLogger(LogoutDetect.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Scheduled(initialDelay = Constant.TASK_INIT_DELAY, fixedRate = Constant.LOGIN_EXPIRE)
    public void loginExpire() {
        //由于全局序列化的配置过,所以redis中取出来的Map全是Map<Object,Object>
        BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.REDIS_USERS_KEY);

        if (null == hashOps) return;

        hashOps.entries().forEach((userId, map) -> {
            if (map instanceof Map) {

                Map<Object, Object> _map = (Map<Object, Object>) map;

                Iterator<Map.Entry<Object, Object>> it = _map.entrySet().iterator();

                while (it.hasNext()) {
                    Object value = it.next().getValue();

                    if (value instanceof Long) {
                        if (System.currentTimeMillis() > Constant.LOGIN_EXPIRE + (Long) value) {
                            it.remove();
                        }
                    }
                }
                hashOps.put(userId, _map);

                logger.info("ID为{}的账号,过期盐清理后账号登录状态:[{}]", userId, _map);
            }
        });
    }
}
