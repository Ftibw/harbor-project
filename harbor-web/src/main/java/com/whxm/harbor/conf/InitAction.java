package com.whxm.harbor.conf;

import com.whxm.harbor.aop.KeepAliveAspect;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.cache.CacheService;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.TerminalService;
import com.whxm.harbor.service.UserService;
import com.whxm.harbor.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author : Ftibw
 * @date : 2018/11/30 22:44
 */
@Component
public class InitAction implements
        ApplicationListener<ContextRefreshedEvent> {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final TerminalService terminalService;
    private final KeepAliveAspect keepAliveAspect;
    private final CacheService cacheService;
    private final UserService userService;

    @Autowired
    public InitAction(RedisTemplate<Object, Object> redisTemplate,
                      TerminalService terminalService,
                      KeepAliveAspect keepAliveAspect,
                      CacheService cacheService,
                      UserService userService) {
        this.redisTemplate = redisTemplate;
        this.terminalService = terminalService;
        this.keepAliveAspect = keepAliveAspect;
        this.cacheService = cacheService;
        this.userService = userService;
    }

    private void initRedisSerializer(RedisTemplate<Object, Object> redisTemplate) {
        //构造函数中配置全局redisTemplate序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        //反序列化的Map将全都是LinkedHashMap<Object,Object>
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initRedisSerializer(redisTemplate);
        keepAliveAspect.initTerminalStatus(terminalService, redisTemplate);

        cacheService.getConfig(TerminalConfig.cacheKey);
        cacheService.getFloorList();
        cacheService.getFormatList();
        cacheService.listBuildings();
        cacheService.listEdges();

        User user = new User();
        user.setUserName("sys_admin");
        user.setUserLoginname("admin");
        user.setUserPassword(MD5Utils.MD5("harbor654321"));
        user.setIsDeleted(Constant.NO);
        User admin = userService.login(user);
        if (null == admin)
            userService.addUser(user);
    }
}
