package com.whxm.harbor.cache;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

@Component
public class CacheKeyGenerator implements KeyGenerator {

    private final Logger logger = LoggerFactory.getLogger(CacheKeyGenerator.class);

    // custom cache key
    private static final int NO_PARAM_KEY = 0;

    private static final int NULL_PARAM_KEY = 53;

    @Override
    public Object generate(Object target, Method method, Object... params) {

        StringBuilder key = new StringBuilder();

        key.append(target.getClass().getSimpleName())
                .append(".")
                .append(method.getName())
                .append(":");

        if (0 == params.length) {

            return key.append(NO_PARAM_KEY).toString();
        }

        for (Object param : params) {

            if (null == param) {

                logger.warn("input null param for Spring cache, use default key={}", NULL_PARAM_KEY);

                key.append(NULL_PARAM_KEY);

            } else if (ClassUtils.isPrimitiveArray(param.getClass())) {

                int length = Array.getLength(param);

                for (int i = 0; i < length; i++) {

                    key.append(Array.get(param, i));

                    key.append(',');
                }

            } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {

                key.append(param);

            } else {

                logger.warn("Using an object as a cache key may lead to unexpected results. Either use @Cacheable(key=..) or implement CacheKey. Method is " + target.getClass() + "#" + method.getName());

                key.append(param.hashCode());
            }

            key.append('-');
        }

        String finalKey = key.toString();

        long cacheKeyHash = Hashing.murmur3_128().hashString(finalKey, Charset.defaultCharset()).asLong();

        logger.debug("using cache key={} hashCode={}", finalKey, cacheKeyHash);

        return key.toString();
    }
}