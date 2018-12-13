package com.whxm.harbor;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import redis.clients.util.SafeEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public static Boolean count(RedisTemplate redisTemplate, String key, int limit, long expire) {
        final List<String> keys = Collections.singletonList(key);
        final List<String> args = Arrays.asList(limit + "", expire + "");
        //该脚本只会写入一次,redis会缓存该脚本
        final String SCRIPT = "local is_exist = redis.call('get', KEYS[1])"
                + "local count = is_exist and tonumber(is_exist) or 0 "
                + "if count < tonumber(ARGV[1]) then "
                + "redis.call('set', KEYS[1],count + 1) "
                + "redis.call('expire',KEYS[1], tonumber(ARGV[2])) "
                + "return 1 "
                + "else return 0 end";
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(SCRIPT);
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.eval(
                        SafeEncoder.encode(SCRIPT),
                        ReturnType.BOOLEAN,
                        keys.size(),
                        getByteParams(getParams(keys, args)));
            }
        });
    }

    private static String[] getParams(List<String> keys, List<String> args) {
        int keyCount = keys.size();
        int argCount = args.size();
        String[] params = new String[keyCount + args.size()];
        for (int i = 0; i < keyCount; i++)
            params[i] = keys.get(i);
        for (int i = 0; i < argCount; i++)
            params[keyCount + i] = args.get(i);
        return params;
    }

    private static byte[][] getByteParams(String... params) {
        byte[][] p = new byte[params.length][];
        for (int i = 0; i < params.length; i++)
            p[i] = SafeEncoder.encode(params[i]);
        return p;
    }
}
