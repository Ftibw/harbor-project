package com.whxm.harbor.conf;

import com.whxm.harbor.filter.MultiplexRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class FilterConfig {
    /** 
     * 配置过滤器 
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();  
        registration.setFilter(getMultiplexRequestFilter());
        registration.addUrlPatterns("/*");
        //给FilterConfig中设置初始参数,在init(FilterConfig config)是使用
        //registration.addInitParameter("paramName", "paramValue");
        registration.setName("MultiplexRequestFilter");
        return registration;  
    }  
  
    /**  
     * 创建一个多种类的方法/类型请求过滤器
     */
    @Bean(name = "MultiplexRequestFilter")
    public Filter getMultiplexRequestFilter() {
        return new MultiplexRequestFilter();
    }  
} 