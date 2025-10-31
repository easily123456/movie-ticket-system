package com.movieticket.service.impl;

import com.movieticket.dto.UserActivityResponse;
import com.movieticket.dto.UserStatsResponse;
import com.movieticket.entity.User;
import com.movieticket.repository.CommentRepository;
import com.movieticket.repository.FavoriteRepository;
import com.movieticket.repository.OrderRepository;
import com.movieticket.repository.UserRepository;
import com.movieticket.service.UserService;
import com.movieticket.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//由Lombok提供，自动生成一个包含所有final字段的构造方法
@Transactional//声明该类中的所有公共方法默认在事务中执行，如果方法抛出RuntimeException，事务会自动回滚
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;

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
    
//    @Override
//    public void batchDeleteUsers(List<Long> userIds) {
//        List<User> users = userRepository.findAllById(userIds);
//        if (users.size() != userIds.size()) {
//            throw new RuntimeException("部分用户不存在");
//        }
//        userRepository.deleteAll(users);
//    }
    
    @Override
    public void batchChangeUserStatus(List<Long> userIds, Boolean status) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new RuntimeException("部分用户不存在");
        }
        users.forEach(user -> user.setStatus(status));
        userRepository.saveAll(users);
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
    
    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        UserStatsResponse stats = new UserStatsResponse();
        
        // 订单统计
        stats.setTotalOrders(orderRepository.countByUser(user));
        stats.setPendingOrders(orderRepository.countByUserAndStatus(user, com.movieticket.entity.Order.OrderStatus.PENDING));
        stats.setPaidOrders(orderRepository.countByUserAndStatus(user, com.movieticket.entity.Order.OrderStatus.PAID));
        stats.setCancelledOrders(orderRepository.countByUserAndStatus(user, com.movieticket.entity.Order.OrderStatus.CANCELLED));
        
        // 消费统计
        BigDecimal totalSpent = orderRepository.sumTotalPriceByUserAndStatus(user, com.movieticket.entity.Order.OrderStatus.PAID);
        stats.setTotalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO);
        
        Long paidOrderCount = orderRepository.countByUserAndStatus(user, com.movieticket.entity.Order.OrderStatus.PAID);
        if (paidOrderCount > 0 && totalSpent != null) {
            stats.setAvgOrderValue(totalSpent.divide(new BigDecimal(paidOrderCount), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.setAvgOrderValue(BigDecimal.ZERO);
        }
        
        // 评论统计
        stats.setTotalComments(commentRepository.countByUser(user));
        Double avgRating = commentRepository.findAverageRatingByUser(user);
        stats.setAvgRating(avgRating != null ? avgRating : 0.0);
        
        // 收藏统计
        stats.setTotalFavorites(favoriteRepository.countByUser(user));
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserActivityResponse> getUserActivities(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
                
        List<UserActivityResponse> activities = new ArrayList<>();
        
        // 获取用户订单活动
        orderRepository.findByUserOrderByCreateTimeDesc(user, Pageable.ofSize(10)).forEach(order -> {
            UserActivityResponse activity = new UserActivityResponse();
            activity.setType("ORDER");
            activity.setDescription("创建订单 #" + order.getOrderNo());
            activity.setCreateTime(order.getCreateTime());
            activity.setDetails(order);
            activities.add(activity);
        });
        
        // 获取用户评论活动
        commentRepository.findByUserOrderByCreateTimeDesc(user, Pageable.ofSize(10)).forEach(comment -> {
            UserActivityResponse activity = new UserActivityResponse();
            activity.setType("COMMENT");
            activity.setDescription("评论电影 (评分: " + comment.getRating() + ")");
            activity.setCreateTime(comment.getCreateTime());
            activity.setDetails(comment);
            activities.add(activity);
        });
        
        // 获取用户收藏活动
        favoriteRepository.findByUserOrderByCreateTimeDesc(user, Pageable.ofSize(10)).forEach(favorite -> {
            UserActivityResponse activity = new UserActivityResponse();
            activity.setType("FAVORITE");
            activity.setDescription("收藏电影: " + favorite.getMovie().getTitle());
            activity.setCreateTime(favorite.getCreateTime());
            activity.setDetails(favorite);
            activities.add(activity);
        });
        
        // 按时间排序并限制数量
        return activities.stream()
                .sorted((a1, a2) -> a2.getCreateTime().compareTo(a1.getCreateTime()))
                .limit(20)
                .collect(Collectors.toList());
    }





    // 批量删除用户
    @Transactional
    @Override
    public int batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }

        int deletedCount = 0;
        for (Long userId : userIds) {
            try {
                userRepository.deleteById(userId);
                deletedCount++;
            } catch (Exception e) {
                log.error("删除用户失败: {}", userId, e);
                // 继续处理其他用户
            }
        }

        return deletedCount;
    }

    // 批量更新用户状态
    @Transactional
    @Override
    public int batchUpdateUserStatus(List<Long> userIds, Boolean status) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }

        int updatedCount = 0;
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
                user.setStatus(status);
                userRepository.save(user);
                updatedCount++;
            } catch (Exception e) {
                log.error("更新用户状态失败: {}", userId, e);
                // 继续处理其他用户
            }
        }

        return updatedCount;
    }

    // 批量设置用户角色
    @Transactional
    @Override
    public int batchUpdateUserRole(List<Long> userIds, User.Role role) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("用户ID列表不能为空");
        }

        int updatedCount = 0;
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
                user.setRole(role);
                userRepository.save(user);
                updatedCount++;
            } catch (Exception e) {
                log.error("更新用户角色失败: {}", userId, e);
                // 继续处理其他用户
            }
        }

        return updatedCount;
    }
}