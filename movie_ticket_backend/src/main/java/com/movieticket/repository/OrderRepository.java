package com.movieticket.repository;

import com.movieticket.entity.Order;
import com.movieticket.entity.User;
import com.movieticket.entity.Session;
import com.movieticket.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // 根据状态统计订单数量
    Long countByStatus(Order.OrderStatus status);

    // 根据状态和时间范围统计订单数量
    Long countByStatusAndCreateTimeBetween(Order.OrderStatus status, LocalDateTime start, LocalDateTime end);

    // 根据状态统计营收
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
    Optional<BigDecimal> sumRevenueByStatus(@Param("status") Order.OrderStatus status);

    // 根据状态和时间范围统计营收
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status AND o.createTime BETWEEN :start AND :end")
    Optional<BigDecimal> sumRevenueByStatusAndTimeRange(
            @Param("status") Order.OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // 获取热门电影统计
    @Query("SELECT o.session.movie.id, o.session.movie.title, COUNT(o), SUM(o.totalPrice), " +
           "(SELECT COUNT(f) FROM Favorite f WHERE f.movie.id = o.session.movie.id), " +
           "o.session.movie.rating " +
           "FROM Order o WHERE o.status = 'PAID' " +
           "GROUP BY o.session.movie.id, o.session.movie.title, o.session.movie.rating " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> findPopularMoviesStats(@Param("limit") int limit);

    // 保留原有方法以确保向后兼容
    List<Order> findByUserAndStatusOrderByCreateTimeDesc(User user, OrderStatus status);

    Page<Order> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.username LIKE %:keyword% OR o.orderNo LIKE %:keyword%")
    Page<Order> searchOrders(String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createTime < :expireTime AND o.status = 'PENDING'")
    List<Order> findExpiredOrders(@Param("expireTime") LocalDateTime expireTime);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID' AND o.createTime BETWEEN :start AND :end")
    BigDecimal getRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = 'PAID' AND o.createTime BETWEEN :start AND :end")
    BigDecimal findRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    long count();
    
    // 用户订单统计相关方法
    long countByUser(User user);
    
    long countByUserAndStatus(User user, OrderStatus status);
    
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user = :user AND o.status = 'PAID'")
    BigDecimal sumTotalPriceByUserAndStatus(@Param("user") User user, OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.session.movie.id = :movieId")
    long countByMovieId(@Param("movieId") Long movieId);

    // 管理端查询方法
    Page<Order> findByOrderNoContainingIgnoreCase(String orderNo, Pageable pageable);

    Page<Order> findByUserUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<Order> findBySessionMovieTitleContainingIgnoreCase(String movieTitle, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    Page<Order> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status")
    Double sumTotalPriceByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status AND o.createTime BETWEEN :start AND :end")
    Double sumTotalPriceByStatusAndCreateTimeBetween(
            @Param("status") Order.OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
            
    Long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findBySessionIdAndStatus(Long sessionId, Order.OrderStatus orderStatus);

    List<Order> findBySessionIdAndStatusAndCreateTimeAfter(Long sessionId, Order.OrderStatus orderStatus , LocalDateTime fifteenMinutesAgo);

    List<Order> findByStatusAndCreateTimeBefore(Order.OrderStatus orderStatus, LocalDateTime fifteenMinutesAgo);
}