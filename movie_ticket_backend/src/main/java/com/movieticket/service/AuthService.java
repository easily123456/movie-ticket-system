package com.movieticket.service;

import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        user.setRole(User.Role.USER);
        user.setStatus(true);
        // createTime 和 updateTime 由 JPA Auditing 自动设置，无需手动设置

        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 检查用户状态
            if (!user.getStatus()) {
                throw new RuntimeException("账号已被禁用");
            }

            // 验证密码
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 更新最后登录时间
                user.setLastLoginTime(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void resetPassword(String username, String email) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("用户名和邮箱不匹配");
        }

        // 指定的加密密码（对应明文：123456）
        String fixedHash = "$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm";
        user.setPassword(fixedHash);
        userRepository.save(user);
    }
}