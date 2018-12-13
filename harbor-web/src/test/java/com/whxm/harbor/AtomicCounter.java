package com.whxm.harbor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.StaticScriptSource;

import java.util.Collections;

/**
 * @author : Ftibw
 * @date : 2018/12/13 10:21
 */
public class AtomicCounter {

    private static final ScriptSource SCRIPT_SOURCE = new StaticScriptSource("local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local expire = ARGV[2] " +
            "local count = tonumber(redis.call('get', key)) or 0 " +
            "if count < limit then " +
            "redis.call('set', key, count + 1) " +
            "redis.call('expire', key, expire) " +
            "return 1 " +
            "else return 0 end");

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
        redisScript.setScriptSource(SCRIPT_SOURCE);
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
