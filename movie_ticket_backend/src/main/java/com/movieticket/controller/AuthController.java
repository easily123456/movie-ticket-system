package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.auth.AuthRequest;
import com.movieticket.dto.request.auth.RegisterRequest;
import com.movieticket.dto.request.auth.ResetPasswordRequest;
import com.movieticket.dto.response.auth.AuthResponse;
import com.movieticket.entity.User;
import com.movieticket.service.AuthService;
import com.movieticket.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            User registeredUser = authService.register(user);
            String token = jwtUtil.generateToken(
                    registeredUser.getUsername(),
                    registeredUser.getId(),
                    registeredUser.getRole().name());

            AuthResponse response = new AuthResponse(registeredUser, token);
            return ResponseEntity.ok(ApiResponse.success("注册成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        Optional<User> userOpt = authService.login(request.getUsername(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getId(),
                    user.getRole().name());

            AuthResponse response = new AuthResponse(user, token);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = authService.checkUsernameExists(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = authService.checkEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String authToken = token.substring(7); // 去掉 "Bearer " 前缀

            if (!jwtUtil.validateToken(authToken)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
            }

            String username = jwtUtil.getUsernameFromToken(authToken);
            Long userId = jwtUtil.getUserIdFromToken(authToken);
            String role = jwtUtil.getRoleFromToken(authToken);

            // 生成新token
            String newToken = jwtUtil.generateToken(username, userId, role);

            // 获取用户信息
            Optional<User> userOpt = authService.checkUsernameExists(username) ? Optional.of(new User())
                    : Optional.empty(); // 这里应该从数据库获取用户信息

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                AuthResponse response = new AuthResponse(user, newToken);
                return ResponseEntity.ok(ApiResponse.success("Token刷新成功", response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token刷新失败"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // JWT令牌是无状态的，登出操作通常由前端删除令牌来实现
        // 后端可以将令牌加入黑名单（如果实现了令牌黑名单机制）
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getUsername(), request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("密码已重置为123456，请使用该密码登录", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}