package com.whxm.harbor.aop;

import com.whxm.harbor.constant.Constant;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class LogoutDetect {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Scheduled(initialDelay = Constant.TASK_INIT_DELAY, fixedRate = Constant.LOGIN_EXPIRE)
    public void loginExpire() {

        BoundSetOperations<Object, Object> setOps = redisTemplate.boundSetOps(Constant.REDIS_USERS_KEY);

        if (null == setOps) return;

        Set<Object> loginKeys = setOps.members();

        loginKeys.stream().map(
                key -> redisTemplate.boundHashOps(key)
        ).forEach(
                hashOps -> hashOps.entries().forEach(
                        (salt, lastTimePoint) -> {
                            if (System.currentTimeMillis() > Constant.LOGIN_EXPIRE + (Long) lastTimePoint) {
                                hashOps.delete(salt);
                            }
                        }
                )
        );
    }
}
