package com.movieticket.service;

import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;
import com.movieticket.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//用于集成 Mockito 框架的功能到单元测试中
//Mockito 是一个流行的 Java 单元测试框架，它的核心目标是简化对依赖项的模拟
class UserServiceTest {

    @Mock//模拟行为，替代真实的 UserRepository
    private UserRepository userRepository;

    @InjectMocks//表示创建 UserServiceImpl 的实例，并自动将模拟的 UserRepository 注入到 UserServiceImpl 中
    private UserServiceImpl userService;

    @Test
    void whenRegisterUser_thenReturnUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);//any(User.class) 表示匹配任何 User 对象

        User savedUser = userService.register(user);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
        //verify(userRepository, times(1))验证userRepository 的 save 方法被调用一次
        //times(1)指定 save() 方法应该被调用 恰好 1 次
    }
}