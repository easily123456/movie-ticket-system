package com.movieticket.service;

import com.movieticket.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Order order);
    void cancelOrder(Long orderId);
    void payOrder(Long orderId);
    void refundOrder(Long orderId);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNo(String orderNo);
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);
    List<Order> getPendingOrdersByUser(Long userId);
    Page<Order> getAllOrders(Pageable pageable);
    Page<Order> searchOrders(String keyword, Pageable pageable);
    List<Order> getExpiredOrders();
    long getOrderCountByStatus(Order.OrderStatus status);
    BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end);
}