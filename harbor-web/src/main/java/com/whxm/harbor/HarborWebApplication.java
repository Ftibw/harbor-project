package com.whxm.harbor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;

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
        factory.setMaxFileSize("10240000KB");
        factory.setMaxRequestSize("10240000KB");
        return factory.createMultipartConfig();
    }

    /*@Override extends SpringBootServletInitializer
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HarborWebApplication.class);
    }*/
}
