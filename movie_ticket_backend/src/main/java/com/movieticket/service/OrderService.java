package com.movieticket.service;

import com.movieticket.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Order order);
    Order cancelOrder(Long orderId);

    // 创建订单（选座）
    Order createSeatOrder(Long userId, Long sessionId, List<String> seatNumbers);

    Order payOrder(Long orderId);
    void refundOrder(Long orderId);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNo(String orderNo);
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);
    List<Order> getPendingOrdersByUser(Long userId);
    Page<Order> getAllOrders(Pageable pageable);
    Page<Order> searchOrders(String keyword, Pageable pageable);
    List<Order> getExpiredOrders();
    long getOrderCountByStatus(Order.OrderStatus status);
    long getOrderCountByStatus(Order.OrderStatus status, LocalDateTime start, LocalDateTime end);
    long getOrderCountByDateRange(LocalDateTime start, LocalDateTime end);
    long getOrderCountByStatusAndDateRange(Order.OrderStatus status, LocalDateTime start, LocalDateTime end);
    BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end);

    Page<Order> searchOrdersByOrderNo(String orderNo, Pageable pageable);
    Page<Order> searchOrdersByUsername(String username, Pageable pageable);
    Page<Order> searchOrdersByMovieTitle(String movieTitle, Pageable pageable);
    Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);
    Page<Order> getOrdersByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    Order updateOrderStatus(Long id, Order.OrderStatus status);
    void deleteOrder(Long id);
    void batchDeleteOrders(List<Long> orderIds);
    void batchUpdateOrderStatus(List<Long> orderIds, Order.OrderStatus status);
    Double getTotalRevenue(LocalDateTime startDateTime, LocalDateTime endDateTime);
    Double getTodayRevenue();
    Double getMonthlyRevenue();

    // 超时取消订单（定时任务调用）
    @Transactional
    void cancelExpiredOrders();
}