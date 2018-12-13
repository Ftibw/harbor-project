package com.whxm.harbor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HarborWebApplicationTests {

    @Test
    public void contextLoads() {
        assertThat(1).isEqualTo(1);
    }

    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void testRedisLuaScript() {
        String key = "testKey";
        int limit = 2;
        long expire = TimeUnit.HOURS.toMillis(2);
        int i=3;
        while (i-->0){
            if (!AtomicCounter.count(redisTemplate, key, limit, expire)) {
                System.err.println("超出" + limit + "次限制");
            }
        }
    }

}