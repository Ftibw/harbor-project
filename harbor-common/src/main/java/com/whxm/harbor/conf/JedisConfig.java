package com.whxm.harbor.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {

    @Value("${jedis.host}")
    private String host;

    @Value("${jedis.port}")
    private Integer port;

    @Bean
    public Jedis getJedis() {
        Jedis jedis = new Jedis(host,port);
        //jedis.auth("redis");
        //jedis.select(0);
        return jedis;
    }
}
