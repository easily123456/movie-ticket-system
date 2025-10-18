package com.movieticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController //返回值会直接作为 HTTP 响应体
@RequestMapping("/test") //所有方法 URL 都以 /api/test 开头
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() { //Map适合快速构建 JSON 响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Movie Ticket System Backend is running!");
        result.put("timestamp", System.currentTimeMillis());//获取当前时间戳
        return result;
    }
}