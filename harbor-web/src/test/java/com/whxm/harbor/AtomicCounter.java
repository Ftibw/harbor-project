package com.whxm.harbor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Collections;

/**
 * @author : Ftibw
 * @date : 2018/12/13 10:21
 */
public class AtomicCounter {

    private static final Logger logger = LoggerFactory.getLogger(AtomicCounter.class);

    private static String sha1;

    static {
        String script;
        try {
            InputStreamReader reader = new InputStreamReader(new ClassPathResource("script.lua").getInputStream());
            CharBuffer buf = CharBuffer.allocate(0x800); // 2K chars (4K bytes))
            StringBuilder sb = new StringBuilder();
            long total = 0;
            while (reader.read(buf) != -1) {
                buf.flip();
                sb.append(buf);
                total += buf.remaining();
                buf.clear();
            }
            logger.info("read " + total + " chars from script.lua");
            sha1 = DigestUtils.sha1DigestAsHex(sb.toString());
        } catch (IOException e) {
            script = "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local expire = ARGV[2] " +
                    "local count = tonumber(redis.call('get', key)) or 0 " +
                    "if count < limit then " +
                    "redis.call('set', key, count + 1) " +
                    "redis.call('expire', key, expire) " +
                    "return 1 " +
                    "else return 0 end";
            sha1 = DigestUtils.sha1DigestAsHex(script);
            logger.error("read failed from script.lua", e);
        }
    }


    private static final RedisScript redisScript = new RedisScript() {
        //脚本字符串进行摘要加密后的16进制字符串
        @Override
        public String getSha1() {
            return sha1;
        }

        //redis执行脚本响应结果的类型
        @Override
        public Class getResultType() {
            return Long.TYPE;
        }

        @Override
        public String getScriptAsString() {
            return null;
        }
    };

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
    public static boolean count(RedisTemplate redisTemplate, String key, int limit, long expire) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        return 1 == (long) redisTemplate.execute(
                redisScript, serializer, serializer,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(expire)
        );
    }
}