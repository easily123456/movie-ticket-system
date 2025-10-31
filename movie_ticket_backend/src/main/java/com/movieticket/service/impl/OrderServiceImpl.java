package com.movieticket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticket.entity.Order;
import com.movieticket.entity.User;
import com.movieticket.entity.Session;
import com.movieticket.repository.OrderRepository;
import com.movieticket.repository.SessionRepository;
import com.movieticket.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ObjectMapper objectMapper;
    private final OrderNoGenerator orderNoGenerator;

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
    public long getOrderCountByStatus(Order.OrderStatus status, LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return orderRepository.countByStatusAndCreateTimeBetween(status, start, end);
        }
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.countByCreateTimeBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrderCountByStatusAndDateRange(Order.OrderStatus status, LocalDateTime start, LocalDateTime end) {
        return orderRepository.countByStatusAndCreateTimeBetween(status, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        BigDecimal revenue = orderRepository.getRevenueBetween(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrdersByOrderNo(String orderNo, Pageable pageable) {
        return orderRepository.findByOrderNoContainingIgnoreCase(orderNo, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrdersByUsername(String username, Pageable pageable) {
        return orderRepository.findByUserUsernameContainingIgnoreCase(username, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrdersByMovieTitle(String movieTitle, Pageable pageable) {
        return orderRepository.findBySessionMovieTitleContainingIgnoreCase(movieTitle, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderRepository.findByCreateTimeBetween(start, end, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();

        // 状态流转验证
        if (order.getStatus() == Order.OrderStatus.PAID && status == Order.OrderStatus.PENDING) {
            throw new RuntimeException("已支付的订单不能回到待支付状态");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED && status != Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("已取消的订单不能修改状态");
        }

        order.setStatus(status);

        // 设置相关时间
        if (status == Order.OrderStatus.PAID) {
            order.setPayTime(LocalDateTime.now());
        } else if (status == Order.OrderStatus.CANCELLED) {
            order.setCancelTime(LocalDateTime.now());
        }

        order.setUpdateTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();

        // 只有特定状态的订单可以删除
        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new RuntimeException("已支付的订单不能删除");
        }

        orderRepository.deleteById(orderId);
    }
    @Override
    @Transactional(readOnly = true)
    public void batchDeleteOrders(List<Long> orderIds) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        for (Order order : orders) {
            // 只有特定状态的订单可以删除
            if (order.getStatus() != Order.OrderStatus.PAID) {
                orderRepository.delete(order);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void batchUpdateOrderStatus(List<Long> orderIds, Order.OrderStatus status) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        for (Order order : orders) {
            try {
                updateOrderStatus(order.getId(), status);
            } catch (RuntimeException e) {
                // 跳过无法更新的订单
                continue;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return orderRepository.sumTotalPriceByStatusAndCreateTimeBetween(Order.OrderStatus.PAID, start, end);
        }
        return orderRepository.sumTotalPriceByStatus(Order.OrderStatus.PAID);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return orderRepository.sumTotalPriceByStatusAndCreateTimeBetween(Order.OrderStatus.PAID, startOfDay, endOfDay);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return orderRepository.sumTotalPriceByStatusAndCreateTimeBetween(Order.OrderStatus.PAID, startOfMonth, endOfMonth);
    }




    // 创建订单（选座）
    @Override
    public Order createSeatOrder(Long userId, Long sessionId, List<String> seatNumbers) {
        // 验证用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        // 验证场次
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("场次不存在");
        }

        Session session = sessionOpt.get();

        // 检查场次状态
        if (!session.getStatus()) {
            throw new RuntimeException("该场次已取消");
        }

        // 检查座位可用性
        if (!sessionService.checkSeatAvailability(sessionId, seatNumbers)) {
            throw new RuntimeException("所选座位已被预订，请重新选择");
        }

        // 检查座位数量
        if (seatNumbers.size() > 6) {
            throw new RuntimeException("一次最多只能选择6个座位");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNoGenerator.generate());
        order.setUser(userOpt.get());
        order.setSession(session);

        try {
            order.setSeatNumbers(objectMapper.writeValueAsString(seatNumbers));
        } catch (Exception e) {
            throw new RuntimeException("座位信息格式错误");
        }

        order.setSeatCount(seatNumbers.size());
        order.setTotalPrice(session.getPrice().multiply(BigDecimal.valueOf(seatNumbers.size())));
        order.setStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // 更新场次座位信息（锁定座位）
        sessionService.updateSessionSeats(sessionId, seatNumbers.size(), true);

        return savedOrder;
    }

    // 支付订单
    @Override
    @Transactional
    public Order payOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();

        // 检查订单状态
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("订单状态异常，无法支付");
        }

        // 检查座位是否仍然可用
        try {
            List<String> seatNumbers = objectMapper.readValue(
                    order.getSeatNumbers(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
            );

            if (!sessionService.checkSeatAvailability(order.getSession().getId(), seatNumbers)) {
                throw new RuntimeException("座位已被其他用户预订，请重新选择");
            }
        } catch (Exception e) {
            throw new RuntimeException("订单数据异常");
        }

        // 更新订单状态
        order.setStatus(Order.OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // 取消订单（释放座位）
    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();

        // 只有待支付的订单可以取消
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("订单无法取消");
        }

        // 更新订单状态
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 释放座位
        sessionService.updateSessionSeats(order.getSession().getId(), order.getSeatCount(), false);

        return savedOrder;
    }

    // 超时取消订单（定时任务调用）
    @Transactional
    @Override
    public void cancelExpiredOrders() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreateTimeBefore(
                Order.OrderStatus.PENDING, fifteenMinutesAgo);

        for (Order order : expiredOrders) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            orderRepository.save(order);

            // 释放座位
            sessionService.updateSessionSeats(order.getSession().getId(), order.getSeatCount(), false);
        }
    }
}