package com.movieticket.dto.response.admin;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalMovies;
    private Long activeMovies;
    private Long totalOrders;
    private Long pendingOrders;
    private Long paidOrders;
    private Long cancelledOrders;
    private Long totalSessions;
    private Long upcomingSessions;
    private Long totalNews;
    private BigDecimal todayRevenue;
    private BigDecimal monthlyRevenue;
}