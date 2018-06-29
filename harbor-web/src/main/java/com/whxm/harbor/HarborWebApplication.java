package com.whxm.harbor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@MapperScan("com.whxm.harbor.mapper")
@EnableScheduling
@EnableCaching
public class HarborWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarborWebApplication.class, args);
    }


    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10240KB");
        factory.setMaxRequestSize("10240KB");
        return factory.createMultipartConfig();
    }

    @Bean("terminalConfig")
    @Qualifier("terminalConfig")
    public Map<String, Object> terminalConfigParams() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("on_off", "00:00-24:00");
        map.put("delay", 10);
        map.put("protect", 300);
        return map;
    }
}
