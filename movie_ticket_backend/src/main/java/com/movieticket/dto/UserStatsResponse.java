package com.movieticket.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserStatsResponse {
  private Long userId;
  private String userName;
  private int totalTickets;

    // 订单统计
    private Long totalOrders;
    private Long pendingOrders;
    private Long paidOrders;
    private Long cancelledOrders;
    
    // 消费统计
    private BigDecimal totalSpent;
    private BigDecimal avgOrderValue;
    
    // 评论统计
    private Long totalComments;
    private Double avgRating;
    
    // 活跃度统计
    private Long totalFavorites;
}