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
    private List<String> seatNumbers;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private Order.OrderStatus status;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
}