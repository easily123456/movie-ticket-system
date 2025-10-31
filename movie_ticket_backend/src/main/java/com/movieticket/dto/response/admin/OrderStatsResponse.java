package com.movieticket.dto.response.admin;

import lombok.Data;
import com.movieticket.entity.Order;
import java.time.LocalDate;
import java.util.Map;

@Data
public class OrderStatsResponse {
    // 原有字段，用于兼容性
    private long totalOrders;
    private Map<Order.OrderStatus, Long> statusCounts;
    
    // 新增字段，用于趋势统计
    private LocalDate date;
    private Integer paidOrders;
    private Integer pendingOrders;
}