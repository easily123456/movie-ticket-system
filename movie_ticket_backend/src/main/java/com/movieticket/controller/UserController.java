package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.user.PasswordChangeRequest;
import com.movieticket.dto.request.user.UserUpdateRequest;
import com.movieticket.dto.response.user.UserProfileResponse;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@RequestHeader("Authorization") String token) {//获取用户信息
        //@RequestHeader("Authorization")从 HTTP 请求头中获取名为 Authorization 的值，并自动绑定到方法的参数 token 上
        //token格式为：Bearer <TOKEN>，机制为JWT

        //Authorization: Bearer <TOKEN>
        try {
            String authToken = token.substring(7); // 去掉 "Bearer " 前缀
            Long userId = jwtUtil.getUserIdFromToken(authToken);//获取用户ID

            Optional<User> userOpt = userService.getUserById(userId);//根据ID获取用户
            if (userOpt.isPresent()) {
                UserProfileResponse response = new UserProfileResponse(userOpt.get());
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setAvatar(request.getAvatar());

                User updatedUser = userService.updateUser(user);
                UserProfileResponse response = new UserProfileResponse(updatedUser);
                return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (RuntimeException e) {//可能存在用户名称重复，因此先被具体的错误处理
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 验证原密码
                if (!userService.login(user.getUsername(), request.getOldPassword()).isPresent()) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("原密码错误"));
                }

                // 从请求体中获取新密码，并将其设置给用户对象
                user.setPassword(request.getNewPassword());
                userService.updateUser(user);

                return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }
}