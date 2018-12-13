package com.whxm.harbor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import java.util.Collections;

/**
 * @author : Ftibw
 * @date : 2018/12/13 10:21
 */
public class AtomicCounter {

    /**
     * 分布式原子计数
     *
     * @param redisTemplate {@link RedisTemplate}
     * @param key           唯一标识
     * @param limit         次数上限
     * @param expire        唯一标识的锁定时间(单位毫秒)
     * @return true 限制次数之内 | false 超出限制次数
     */
    @SuppressWarnings("unchecked")
    public static Boolean count(RedisTemplate redisTemplate, String key, int limit, long expire) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("script.lua"));
        redisScript.setResultType(Long.TYPE);
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        return 1 == (long) redisTemplate.execute(
                redisScript, serializer, serializer,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(expire)
        );
    }
}
