package com.movieticket.dto.response.admin;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsResponse {
    // 基础统计
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersToday;
    private Long totalMovies;
    private Long activeMovies;
    private Long totalOrders;
    private Long pendingOrders;
    private Long paidOrders;
    private Long cancelledOrders;
    private Long totalSessions;
    private Long upcomingSessions;
    private Long totalNews;
    private Long activeNews;
    
    // 营收统计
    private BigDecimal todayRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    
    // 图表数据
    private List<RevenueDataResponse> weeklyRevenueData;
    private List<MovieStatsResponse> popularMovies;
    private List<OrderStatsResponse> orderTrends;
    private Map<String, Long> userRegistrations;
    
    // 系统信息
    private Integer activeSessions;
    private Double systemLoad;
    private Long totalComments;
    private Long totalFavorites;
}