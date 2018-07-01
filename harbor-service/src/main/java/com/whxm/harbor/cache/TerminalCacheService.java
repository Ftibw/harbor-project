package com.whxm.harbor.cache;

import com.whxm.harbor.conf.TerminalConfig;
import com.whxm.harbor.flag.MementoIF;
import com.whxm.harbor.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 因为未实现接口,
 * 不会采用JdkSerializationRedisSerializer序列化器(可以序列化自定义对象的返回值)
 * 而是使用StringRedisSerializer序列化器(只能序列化String类型的返回值)
 */
@Service
@Transactional
public class TerminalCacheService {

    private final Logger logger = LoggerFactory.getLogger(TerminalCacheService.class);

    private final TerminalConfig terminalConfig;

    private final MementoIF memento;

    @Autowired
    public TerminalCacheService(TerminalConfig terminalConfig) {

        this.terminalConfig = terminalConfig;

        this.memento = terminalConfig.createMemento();
    }

    @CacheEvict(cacheNames = "terminal", key = "#config.cacheKey")
    public TerminalConfig updateConfig(TerminalConfig config) {

        logger.info("update terminal config:{} at:{}", config, new Date());

        BeanUtils.copyProperties(config, terminalConfig);

        return config;
    }

    @Cacheable(cacheNames = "terminal", key = "#cacheKey")
    public String getConfig(String cacheKey) {

        logger.info("get terminal config cacheKey:{} at:{}", cacheKey, new Date());

        TerminalConfig config = new TerminalConfig();

        BeanUtils.copyProperties(terminalConfig, config);

        return JacksonUtils.toJson(config);
    }

    @CacheEvict(cacheNames = "terminal", key = "#cacheKey")
    public void resetConfig(String cacheKey) {

        terminalConfig.restoreMemento(memento);

        logger.info("reset terminal config cacheKey:{} at:{}", cacheKey, new Date());
    }
}
