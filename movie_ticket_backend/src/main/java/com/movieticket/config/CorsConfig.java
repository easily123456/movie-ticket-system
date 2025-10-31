package com.movieticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // 允许所有源，生产环境应该配置具体域名
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的请求方法
        configuration.setAllowedHeaders(Arrays.asList("*"));//允许的请求头
        configuration.setAllowCredentials(true);//允许跨域请求携带凭证(Cookie)
        configuration.setMaxAge(3600L);//预检请求（OPTIONS）的缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 创建一个 UrlBasedCorsConfigurationSource 对象，用于管理基于 URL 路径的 CORS 配置
        source.registerCorsConfiguration("/api/**", configuration);
        // 将 CORS 配置注册到指定的 URL 路径下
        return source;
    }
}