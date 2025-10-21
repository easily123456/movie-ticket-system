package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.auth.LoginRequest;
import com.movieticket.dto.request.auth.RegisterRequest;
import com.movieticket.dto.response.auth.LoginResponse;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor//为类中所有 final 字段 或 @NonNull 标记的字段 生成一个包含这些字段的构造函数
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        //ResponseEntity为Spring 提供的封装 HTTP 响应的类，可以自定义状态码、响应头、响应体
        //@Valid如果 RegisterRequest 中的字段验证失败，Spring 会自动返回 400 Bad Request
        //@RequestBody将请求体中的 JSON 数据映射为 RegisterRequest 对象
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            User registeredUser = userService.register(user);
            return ResponseEntity.ok(ApiResponse.success("注册成功", registeredUser));
            //返回注册成功的用户信息
        } catch (RuntimeException e) {
            //RuntimeException 是一个广义的异常类，通常用于表示程序运行时的非预期错误
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
            //返回400Bad Request，定义响应体为错误信息
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.login(request.getUsername(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            LoginResponse response = new LoginResponse(user, token);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) { //查询用户是否存在
        //@RequestParam将请求参数映射为方法参数
        boolean exists = userService.getUserByUsername(username).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.getUserByUsername(email).isPresent();
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}