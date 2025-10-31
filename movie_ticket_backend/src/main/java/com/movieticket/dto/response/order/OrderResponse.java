package com.movieticket.dto.response.order;

import com.movieticket.entity.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private Long sessionId;
    private String movieTitle;
    private LocalDateTime sessionTime;
    private String hallName;
    private String seatNumbers;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private Order.OrderStatus status;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.orderNo = order.getOrderNo();
        this.userId = order.getUser().getId();
        this.username = order.getUser().getUsername();
        this.sessionId = order.getSession().getId();
        this.movieTitle = order.getSession().getMovie().getTitle();
        this.sessionTime = order.getSession().getStartTime();
        this.hallName = order.getSession().getHall().getName();
        this.seatNumbers = order.getSeatNumbers();
        this.seatCount = order.getSeatCount();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.payTime = order.getPayTime();
        this.cancelTime = order.getCancelTime();
        this.createTime = order.getCreateTime();
    }
}