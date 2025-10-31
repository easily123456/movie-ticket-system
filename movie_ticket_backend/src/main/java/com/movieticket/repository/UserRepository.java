package com.movieticket.repository;

import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 根据状态统计用户数量
    Long countByStatus(Boolean status);

    // 根据时间范围统计用户数量
    Long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    // 获取用户注册趋势
    @Query("SELECT DATE(u.createTime), COUNT(u) FROM User u " +
           "WHERE u.createTime BETWEEN :start AND :end " +
           "GROUP BY DATE(u.createTime) " +
           "ORDER BY DATE(u.createTime)")
    List<Object[]> findUserRegistrationsByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // 保留原有方法以确保向后兼容
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword")
    Page<User> searchUsers(String keyword, Pageable pageable);
}