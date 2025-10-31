package com.movieticket.service;

import com.movieticket.dto.response.admin.*;

import java.math.BigDecimal;
import java.util.*;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats();

    // 获取今日新增用户
    Long getNewUsersToday();

    // 获取即将开始的场次数量
    Long getUpcomingSessionsCount();

    // 获取今日营收
    BigDecimal getTodayRevenue();

    // 获取本周营收
    BigDecimal getWeeklyRevenue();

    // 获取本月营收
    BigDecimal getMonthlyRevenue();

    // 获取总营收
    BigDecimal getTotalRevenue();

    // 获取周营收数据
    List<RevenueDataResponse> getWeeklyRevenueData();

    // 获取热门电影统计
    List<MovieStatsResponse> getPopularMoviesStats();

    // 获取订单趋势
    List<OrderStatsResponse> getOrderTrends();

    // 获取用户注册数据
    Map<String, Long> getUserRegistrationsData();

    // 获取活跃场次数量（模拟）
    Integer getActiveSessionsCount();

    // 获取系统负载（模拟）
    Double getSystemLoad();

    // 转换电影统计数据
    MovieStatsResponse convertToMovieStats(Object[] result);

    // 保留原有方法，以防其他地方还在使用
    RevenueDataResponse getRevenueData(String range);
    OrderStatsResponse getOrderStats();
}