package com.movieticket.service.impl;

import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;
import com.movieticket.service.UserService;
import com.movieticket.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor//由Lombok提供，自动生成一个包含所有final字段的构造方法
@Transactional//声明该类中的所有公共方法默认在事务中执行，如果方法抛出RuntimeException，事务会自动回滚
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User register(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("用户名已存在");
            //内置的异常类，用于处理运行时异常
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("邮箱已被注册");
            //内置的异常类，用于处理运行时异常
        }
        //加密密码
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setStatus(true);
        return userRepository.save(user);//保存至数据库
        //UserRepository继承于JpaRepository，JpaRepository提供save()方法
    }

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){//判断用户是否存在
            User user = userOpt.get();
            if(PasswordUtil.matches(password,user.getPassword()) && user.getStatus()){
                user.setLastLoginTime(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);//返回一个包装传入的user对象的Optional容器对象
            }
        }
        return Optional.empty();
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        //orElseThrow()判断Optional是否为空，如果为空则抛出异常

        if(!existingUser.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("用户名已存在");
        }
        //改名操作，先判断传入的用户名和数据库存储的用户名是否不同，再判断数据库中是否已存在该用户名

        if(!existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("邮箱已被使用");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAvatar(user.getAvatar());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)//声明只读事务，优化查询性能，明确语义
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

    @Override
    public User changeUserStatus(Long id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByStatus(true);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }
}
