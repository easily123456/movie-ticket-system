package com.movieticket.service;

import com.movieticket.dto.UserActivityResponse;
import com.movieticket.dto.UserStatsResponse;
import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
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

    // 获取所有用户（用于导出）
    List<User> getAllUsers();

    // 批量删除用户
    int batchDeleteUsers(List<Long> userIds);
    
    // 批量更改用户状态
    void batchChangeUserStatus(List<Long> userIds, Boolean status);
    
    // 获取用户统计数据
    UserStatsResponse getUserStats(Long userId);
    
    // 获取用户活动记录
    List<UserActivityResponse> getUserActivities(Long userId);

    // 批量更新用户状态
    int batchUpdateUserStatus(List<Long> userIds, Boolean status);

    // 批量设置用户角色
    int batchUpdateUserRole(List<Long> userIds, User.Role role);
}