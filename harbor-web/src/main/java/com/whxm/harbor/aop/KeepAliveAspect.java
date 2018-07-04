package com.whxm.harbor.aop;

import com.whxm.harbor.bean.BizTerminal;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.TerminalService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class KeepAliveAspect {

    private final Logger logger = LoggerFactory.getLogger(KeepAliveAspect.class);

    private final RedisTemplate<Object, Object> redisTemplate;

    private final TerminalService terminalService;

    @Autowired
    public KeepAliveAspect(TerminalService terminalService, RedisTemplate<Object, Object> redisTemplate) {

        //构造函数中配置全局redisTemplate序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        //------------------------------------------------------------------------------------

        this.terminalService = terminalService;

        this.redisTemplate = redisTemplate;

        final BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.TERMINAL_STATUS_KEY);

        PageVO<BizTerminal> pageVO = terminalService.getBizTerminalList(new PageQO(), null);

        pageVO.getList().forEach(item -> hashOps.putIfAbsent(item.getTerminalNumber(), System.currentTimeMillis()));
    }

    @Around("@within(com.whxm.harbor.annotation.KeepAliveDetect)||@annotation(com.whxm.harbor.annotation.KeepAliveDetect)")
    public Object visitLogPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        String terminalNumber = String.valueOf(joinPoint.getArgs()[0]);

        BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.TERMINAL_STATUS_KEY);

        hashOps.put(terminalNumber, System.currentTimeMillis());

        terminalService.updateTerminalOnline(terminalNumber);

        return joinPoint.proceed();
    }

    @Scheduled(initialDelay = Constant.TASK_INIT_DELAY, fixedRate = Constant.KEEP_ALIVE_INTERVAL)
    public void keepAliveDetect() {

        BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.TERMINAL_STATUS_KEY);

        Map<Object, Object> map = hashOps.entries();

        List<Object> keys = new ArrayList<>();

        map.forEach((terminalNumber, lastTimePoint) -> {

            Long timePoint = (Long) lastTimePoint;

            if (0 == timePoint) return;

            if (System.currentTimeMillis() > timePoint + Constant.KEEP_ALIVE_INTERVAL) {

                keys.add(String.valueOf(terminalNumber));

                hashOps.put(terminalNumber, 0);
            }
        });

        if (!keys.isEmpty()) {

            terminalService.updateTerminalOffline(keys);

            //hashOps.delete(keys.toArray());

            logger.info("编号为{}的终端离线", keys);
        }
    }

}
