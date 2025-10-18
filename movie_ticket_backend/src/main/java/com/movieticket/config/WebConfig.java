package com.movieticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}") //读取yaml中的app.cors.allowed-origins
    private String allowedOrigins;//app.cors.allowed-origins的值注入到allowedOrigins中

    @Override
    public void addCorsMappings(CorsRegistry registry) { // CORS（跨域资源共享）配置
        // 允许跨域访问的接口
        registry.addMapping("/api/**") //URL 路径需要应用的 CORS 规则
                .allowedOrigins(allowedOrigins.split( ",")) //允许跨域访问的域名列表，划分出两个域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //允许跨域访问的请求方法，options检查是否允许跨域
                .allowedHeaders("*")//允许跨域访问的请求头
                .allowCredentials(true)//允许跨域请求携带凭证
                .maxAge(3600);//预检请求（OPTIONS）的缓存时间

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
        //用户访问 /uploads/** 映射到 ./uploads/ 目录下的文件
    }
}
