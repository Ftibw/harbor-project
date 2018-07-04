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

    public Boolean lock(String lockKey, String requestId, long expireTime) {

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

            if (null != ret) logger.info("key为[{}]的redis分布式锁加锁{}", lockKey, new String((byte[]) ret));

            return ret != null;
        });
    }

    public Boolean unlock(String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
                + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";

        List<String> keys = Collections.singletonList(lockKey);

        List<String> args = Collections.singletonList(requestId);

        return luaTemplate(keys, args, script, Boolean.TYPE, ReturnType.BOOLEAN);

    }

    public <T> T luaTemplate(List<String> keys, List<String> args, String script, Class<T> returnClass, ReturnType returnType) {

        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();

        redisScript.setScriptText(script);

        return redisTemplate.execute(
                (RedisCallback<T>) connection -> connection.eval(
                        SafeEncoder.encode(script),
                        returnType,
                        keys.size(),
                        getByteParams(getParams(keys, args))
                )
        );
    }

    public <T> T luaTemplate(String script, Class<T> returnClass, ReturnType returnType) {

        List<String> empty = Collections.singletonList("");

        return luaTemplate(empty, empty, script, returnClass, returnType);
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
//关于ReturnType的说明,见代码
/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
public class JedisScriptReturnConverter implements Converter<Object, Object> {

    private final ReturnType returnType;

    public JedisScriptReturnConverter(ReturnType returnType) {
        this.returnType = returnType;
    }

    @SuppressWarnings("unchecked")
    public Object convert(Object result) {
        if (result instanceof String) {
            // evalsha converts byte[] to String. Convert back for consistency
            return SafeEncoder.encode((String) result);
        }
        if (returnType == ReturnType.STATUS) {
            return JedisConverters.toString((byte[]) result);
        }
        if (returnType == ReturnType.BOOLEAN) {
            // Lua false comes back as a null bulk reply
            if (result == null) {
                return Boolean.FALSE;
            }
            return ((Long) result == 1);
        }
        if (returnType == ReturnType.MULTI) {
            List<Object> resultList = (List<Object>) result;
            List<Object> convertedResults = new ArrayList<Object>();
            for (Object res : resultList) {
                if (res instanceof String) {
                    // evalsha converts byte[] to String. Convert back for
                    // consistency
                    convertedResults.add(SafeEncoder.encode((String) res));
                } else {
                    convertedResults.add(res);
                }
            }
            return convertedResults;
        }
        return result;
    }
}
* */