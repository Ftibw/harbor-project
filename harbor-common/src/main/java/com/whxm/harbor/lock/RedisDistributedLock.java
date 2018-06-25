package com.whxm.harbor.lock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.util.SafeEncoder;
import java.util.Collections;
import java.util.List;

/**
 * 本质上还是乐观锁
 */
@Component
public class RedisDistributedLock {

    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public Boolean lock(String lockKey, String requestId, int expireTime) {

        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {

            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            //成功则返回的字符串"OK"的字节数组,失败则返回null
            Object ret = connection.execute(
                    "set",
                    serializer.serialize(lockKey),
                    serializer.serialize(requestId),
                    serializer.serialize(SET_IF_NOT_EXIST),
                    serializer.serialize(SET_WITH_EXPIRE_TIME),
                    SafeEncoder.encode(String.valueOf(expireTime))
            );

            if (null != ret) logger.info("redis分布式锁加锁{}", new String((byte[]) ret));

            return ret != null;
        });
    }

    public Boolean unlock(String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
                + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";

        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();

        redisScript.setScriptText(script);

        List<String> keys = Collections.singletonList(lockKey);
        List<String> args = Collections.singletonList(requestId);

        return redisTemplate.execute(
                (RedisCallback<Boolean>) connection -> connection.eval(
                        SafeEncoder.encode(script),
                        ReturnType.BOOLEAN,
                        keys.size(),
                        getByteParams(getParams(keys, args))
                )
        );
    }

    //恶心,参数序列化格式连文档说明都没有,还是对比jedis源码,然后粘贴过来的...
    private String[] getParams(List<String> keys, List<String> args) {
        int keyCount = keys.size();
        int argCount = args.size();

        String[] params = new String[keyCount + args.size()];

        for (int i = 0; i < keyCount; i++)
            params[i] = keys.get(i);

        for (int i = 0; i < argCount; i++)
            params[keyCount + i] = args.get(i);

        return params;
    }

    private byte[][] getByteParams(String... params) {
        byte[][] p = new byte[params.length][];
        for (int i = 0; i < params.length; i++)
            p[i] = SafeEncoder.encode(params[i]);

        return p;
    }

}