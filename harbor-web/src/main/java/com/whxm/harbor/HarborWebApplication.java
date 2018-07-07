package com.whxm.harbor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.MultipartConfigElement;

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
        //factory.setLocation("");//设置文件上传临时目录的父目录,默认项目路径为父目录
        factory.setMaxFileSize("10240000KB");
        factory.setMaxRequestSize("10240000KB");
        return factory.createMultipartConfig();
    }

    /*@Override extends SpringBootServletInitializer
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HarborWebApplication.class);
    }*/
}
