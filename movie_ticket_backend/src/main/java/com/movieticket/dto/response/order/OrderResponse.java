package com.movieticket.dto.response.order;

import com.movieticket.entity.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
    private List<String> seatNumbers;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private Order.OrderStatus status;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private String moviePoster;

    public OrderResponse(Order order) {
        try {
            this.id = order.getId();
            this.orderNo = order.getOrderNo();
            this.userId = order.getUser().getId();
            this.username = order.getUser().getUsername();
            this.sessionId = order.getSession().getId();
            this.movieTitle = order.getSession().getMovie().getTitle();
            this.sessionTime = order.getSession().getStartTime();
            this.hallName = order.getSession().getHall().getName();
            // 解析存储在数据库中的 seatNumbers JSON 字符串为 List<String>
            ObjectMapper mapper = new ObjectMapper();
            if (order.getSeatNumbers() != null) {
                this.seatNumbers = mapper.readValue(order.getSeatNumbers(), new TypeReference<List<String>>() {
                });
            }
            this.seatCount = order.getSeatCount();
            this.totalPrice = order.getTotalPrice();
            this.status = order.getStatus();
            this.payTime = order.getPayTime();
            this.cancelTime = order.getCancelTime();
            this.createTime = order.getCreateTime();
            this.moviePoster = order.getSession().getMovie().getPosterUrl();
        } catch (Exception e) {
            throw new RuntimeException("构造 OrderResponse 失败", e);
        }
    }
}