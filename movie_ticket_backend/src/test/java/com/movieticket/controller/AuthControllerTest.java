package com.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.dto.request.auth.RegisterRequest;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;  // Spring 提供的模拟 MVC 环境，用于模拟 HTTP 请求和验证响应
    private ObjectMapper objectMapper; //Jackson 的 JSON 处理工具，用于对象与 JSON 之间的转换

    @Mock //模拟 UserService，不调用真实实现
    private UserService userService;

    @Mock //模拟 UserService，不调用真实实现
    private JwtUtil jwtUtil;

    @InjectMocks //创建被测试的 AuthController 实例，并自动注入模拟的依赖项
    private AuthController authController;

    @BeforeEach //在每个测试方法执行前运行
    void setUp() {
        objectMapper = new ObjectMapper(); // 手动创建实例
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build(); //使用 MockMvcBuilders 构建 mockMvc 实例，设置要测试的控制器
    }

    @Test
    void whenRegisterValidUser_thenReturnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setPhone("13800138000");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userService.register(any(User.class))).thenReturn(user);
        //当 userService.register() 方法被调用时，无论传入的是什么 User 对象，都返回预设的 user 实例。
        // 这是为了模拟服务层的行为，而无需真正执行业务逻辑。

        mockMvc.perform(post("/api/auth/register") //发送 POST 请求到 /api/auth/register
                        .contentType(MediaType.APPLICATION_JSON) //设置内容类型为 JSON
                        .content(objectMapper.writeValueAsString(request))) //将 RegisterRequest 对象序列化为 JSON 字符串
                .andExpect(status().isOk()) //验证 HTTP 状态码为 200
                .andExpect(jsonPath("$.success").value(true)) //验证 JSON 响应中的 "success" 字段为 true
                .andExpect(jsonPath("$.message").value("注册成功")) //验证 JSON 响应中的 "message" 字段为 "注册成功"
                .andExpect(jsonPath("$.data.username").value("testuser")); //验证 JSON 响应中的 "data.username" 字段为 "testuser"
        //jsonPath("$.data.username") 是 Spring MVC Test 框架中的一个表达式，用于从 JSON 响应中提取特定字段的值进行验证
        //$：表示 JSON 文档的根节点
        //.data：访问根节点下的 data 字段
        //.username：访问 data 对象下的 username 字段

    }
}