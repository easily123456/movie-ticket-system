package com.movieticket.service.impl;

import com.movieticket.entity.Order;
import com.movieticket.entity.User;
import com.movieticket.entity.Session;
import com.movieticket.repository.OrderRepository;
import com.movieticket.service.OrderService;
import com.movieticket.service.UserService;
import com.movieticket.service.SessionService;
import com.movieticket.util.OrderNoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final SessionService sessionService;

    @Override
    public Order createOrder(Order order) {
        // 验证用户和场次存在
        User user = userService.getUserById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Session session = sessionService.getSessionById(order.getSession().getId())
                .orElseThrow(() -> new RuntimeException("场次不存在"));

        // 检查场次是否可用
        if (!session.getStatus()) {
            throw new RuntimeException("场次已取消");
        }

        // 检查座位是否可用
        if (session.getBookedSeats() + order.getSeatCount() > session.getAvailableSeats()) {
            throw new RuntimeException("座位数量不足");
        }

        // 生成订单号
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUser(user);
        order.setSession(session);
        order.setStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        sessionService.updateBookedSeats(session.getId(), order.getSeatCount());
        // 更新场次的可用座位数（相加）

        return savedOrder;
    }

    @Override
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 只能更新特定字段
        existingOrder.setSeatNumbers(order.getSeatNumbers());
        existingOrder.setSeatCount(order.getSeatCount());
        existingOrder.setTotalPrice(order.getTotalPrice());

        return orderRepository.save(existingOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能取消待支付的订单");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderRepository.save(order);

        // 释放座位
        Session session = order.getSession();
        session.setBookedSeats(session.getBookedSeats() - order.getSeatCount());
    }

    @Override
    public void payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能支付待支付的订单");
        }

        order.setStatus(Order.OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("只能退款已支付的订单");
        }

        order.setStatus(Order.OrderStatus.REFUNDED);
        order.setCancelTime(LocalDateTime.now());
        orderRepository.save(order);

        // 释放座位
        Session session = order.getSession();
        session.setBookedSeats(session.getBookedSeats() - order.getSeatCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return orderRepository.findByUserOrderByCreateTimeDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getPendingOrdersByUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return orderRepository.findByUserAndStatusOrderByCreateTimeDesc(user, Order.OrderStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrders(String keyword, Pageable pageable) {
        return orderRepository.searchOrders(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getExpiredOrders() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15); // 获取当前时间减去15分钟的时间
        return orderRepository.findExpiredOrders(expireTime);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        BigDecimal revenue = orderRepository.getRevenueBetween(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}