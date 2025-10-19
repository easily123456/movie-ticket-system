package com.movieticket.service;

import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> login(String username, String password);
    User updateUser(User user);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> searchUsers(String keyword, Pageable pageable);
    User  changeUserStatus(Long id,Boolean status);//Boolean允许接收null可能会出问题
    long getTotalUserCount();
    long getActiveUserCount();
    void updateLastLoginTime(Long userId);
}
