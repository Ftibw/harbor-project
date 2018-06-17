package com.whxm.harbor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@MapperScan("com.whxm.harbor.mapper")
public class HarborWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarborWebApplication.class, args);
    }

    /*@Override   extends SpringBootServletInitializer
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HarborWebApplication.class);
    }*/
}
