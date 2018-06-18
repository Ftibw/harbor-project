package com.whxm.harbor.lock;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 本质上还是乐观锁
 */
@Component
public class RedisDistributedLock {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    @Autowired
    private Jedis jedis;

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public boolean tryAcquire(String lockKey, String requestId, int expireTime) {
        //1.
        //client.set(SafeEncoder.encode(key), SafeEncoder.encode(value), SafeEncoder.encode(nxxx),
        //      SafeEncoder.encode(expx), time)
        //2.
        //binaryClient.sendCommand(Command.SET, key, value, nxxx, expx, toByteArray(time));
        //方法签名:protected Connection sendCommand(final Command cmd, final byte[]... args);
        //3.
        //最后在sendCommand中调用了Protocol.sendCommand(outputStream, cmd, args);

        //说明了参数对应的条件的设置都是在redis中完成的,保证了原子性
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        return LOCK_SUCCESS.equals(result);
    }

    /**
     * 尝试释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean tryRelease(String lockKey, String requestId) {

        //多么熟悉的lua脚本,游戏程序员的基础语言...
        //所有的check and act都放到了redis中,保证了原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
                + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";

        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        return RELEASE_SUCCESS.equals(result);
    }

}