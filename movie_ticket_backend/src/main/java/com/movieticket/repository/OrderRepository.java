package com.movieticket.repository;

import com.movieticket.entity.Order;
import com.movieticket.entity.User;
import com.movieticket.entity.Session;
import com.movieticket.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUserAndStatusOrderByCreateTimeDesc(User user, OrderStatus status);

    Page<Order> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.username LIKE %:keyword% OR o.orderNo LIKE %:keyword%")
    Page<Order> searchOrders(String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createTime < :expireTime AND o.status = 'PENDING'")
    List<Order> findExpiredOrders(@Param("expireTime") LocalDateTime expireTime);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID' AND o.createTime BETWEEN :start AND :end")
    BigDecimal getRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}