package com.movieticket.service.impl;

import com.movieticket.dto.response.admin.*;
import com.movieticket.repository.*;
import com.movieticket.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;

    // 获取仪表盘统计数据
    @Override
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        
        // 用户统计
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByStatus(true));
        stats.setNewUsersToday(getNewUsersToday());
        
        // 电影统计
        stats.setTotalMovies(movieRepository.count());
        stats.setActiveMovies(movieRepository.countByStatus(true));
        
        // 订单统计
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus(com.movieticket.entity.Order.OrderStatus.PENDING));
        stats.setPaidOrders(orderRepository.countByStatus(com.movieticket.entity.Order.OrderStatus.PAID));
        stats.setCancelledOrders(orderRepository.countByStatus(com.movieticket.entity.Order.OrderStatus.CANCELLED));
        
        // 场次统计
        stats.setTotalSessions(sessionRepository.count());
        stats.setUpcomingSessions(getUpcomingSessionsCount());
        
        // 资讯统计
        stats.setTotalNews(newsRepository.count());
        stats.setActiveNews(newsRepository.countByStatus(true));
        
        // 营收统计
        stats.setTodayRevenue(getTodayRevenue());
        stats.setWeeklyRevenue(getWeeklyRevenue());
        stats.setMonthlyRevenue(getMonthlyRevenue());
        stats.setTotalRevenue(getTotalRevenue());
        
        // 图表数据
        stats.setWeeklyRevenueData(getWeeklyRevenueData());
        stats.setPopularMovies(getPopularMoviesStats());
        stats.setOrderTrends(getOrderTrends());
        stats.setUserRegistrations(getUserRegistrationsData());
        
        // 系统信息
        stats.setTotalComments(commentRepository.count());
        stats.setTotalFavorites(favoriteRepository.count());
        stats.setActiveSessions(getActiveSessionsCount());
        stats.setSystemLoad(getSystemLoad());
        
        return stats;
    }

    // 获取今日新增用户
    @Override
    public Long getNewUsersToday() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return userRepository.countByCreateTimeBetween(startOfDay, endOfDay);
    }

    // 获取即将开始的场次数量
    @Override
    public Long getUpcomingSessionsCount() {
        return sessionRepository.countByStartTimeAfterAndStatus(LocalDateTime.now(), true);
    }

    // 获取今日营收
    @Override
    public BigDecimal getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return orderRepository.sumRevenueByStatusAndTimeRange(
            com.movieticket.entity.Order.OrderStatus.PAID, startOfDay, endOfDay
        ).orElse(BigDecimal.ZERO);
    }

    // 获取本周营收
    @Override
    public BigDecimal getWeeklyRevenue() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        LocalDateTime endOfWeek = LocalDateTime.now();
        return orderRepository.sumRevenueByStatusAndTimeRange(
            com.movieticket.entity.Order.OrderStatus.PAID, startOfWeek, endOfWeek
        ).orElse(BigDecimal.ZERO);
    }

    // 获取本月营收
    @Override
    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.now();
        return orderRepository.sumRevenueByStatusAndTimeRange(
            com.movieticket.entity.Order.OrderStatus.PAID, startOfMonth, endOfMonth
        ).orElse(BigDecimal.ZERO);
    }

    // 获取总营收
    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.sumRevenueByStatus(com.movieticket.entity.Order.OrderStatus.PAID)
                .orElse(BigDecimal.ZERO);
    }

    // 获取周营收数据
    @Override
    public List<RevenueDataResponse> getWeeklyRevenueData() {
        List<RevenueDataResponse> revenueData = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            BigDecimal revenue = orderRepository.sumRevenueByStatusAndTimeRange(
                com.movieticket.entity.Order.OrderStatus.PAID, startOfDay, endOfDay
            ).orElse(BigDecimal.ZERO);
            
            Long orderCount = orderRepository.countByStatusAndCreateTimeBetween(
                com.movieticket.entity.Order.OrderStatus.PAID, startOfDay, endOfDay
            );
            
            Long userCount = userRepository.countByCreateTimeBetween(startOfDay, endOfDay);
            
            RevenueDataResponse data = new RevenueDataResponse();
            data.setDate(date);
            data.setRevenue(revenue);
            data.setOrderCount(orderCount.intValue());
            data.setUserCount(userCount.intValue());
            
            revenueData.add(data);
        }
        
        return revenueData;
    }

    // 获取热门电影统计
    @Override
    public List<MovieStatsResponse> getPopularMoviesStats() {
        List<Object[]> results = orderRepository.findPopularMoviesStats(10);
        return results.stream().map(this::convertToMovieStats).collect(Collectors.toList());
    }

    // 获取订单趋势
    @Override
    public List<OrderStatsResponse> getOrderTrends() {
        List<OrderStatsResponse> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            Long paidOrders = orderRepository.countByStatusAndCreateTimeBetween(
                com.movieticket.entity.Order.OrderStatus.PAID, startOfDay, endOfDay
            );
            
            Long pendingOrders = orderRepository.countByStatusAndCreateTimeBetween(
                com.movieticket.entity.Order.OrderStatus.PENDING, startOfDay, endOfDay
            );
            
            OrderStatsResponse trend = new OrderStatsResponse();
            trend.setDate(date);
            trend.setPaidOrders(paidOrders.intValue());
            trend.setPendingOrders(pendingOrders.intValue());
            
            trends.add(trend);
        }
        
        return trends;
    }

    // 获取用户注册数据
    @Override
    public Map<String, Long> getUserRegistrationsData() {
        Map<String, Long> registrations = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            Long count = userRepository.countByCreateTimeBetween(startOfDay, endOfDay);
            registrations.put(date.toString(), count);
        }
        
        return registrations;
    }

    // 获取活跃场次数量（模拟）
    @Override
    public Integer getActiveSessionsCount() {
        // 这里可以集成实际的会话管理
        return new Random().nextInt(100) + 50;
    }

    // 获取系统负载（模拟）
    @Override
    public Double getSystemLoad() {
        // 这里可以集成实际的系统监控
        return new Random().nextDouble() * 100;
    }

    // 转换电影统计数据
    @Override
    public MovieStatsResponse convertToMovieStats(Object[] result) {
        MovieStatsResponse stats = new MovieStatsResponse();
        stats.setMovieId((Long) result[0]);
        stats.setMovieTitle((String) result[1]);
        stats.setOrderCount(((Long) result[2]).intValue());
        stats.setRevenue((BigDecimal) result[3]);
        stats.setFavoriteCount(((Long) result[4]).intValue());
        stats.setRating(((BigDecimal) result[5]).doubleValue());
        return stats;
    }
    
    // 保留原有方法实现，以防其他地方还在使用
    @Override
    public RevenueDataResponse getRevenueData(String range) {
        // 空实现或可以根据需要保留原始实现
        return new RevenueDataResponse();
    }
    
    @Override
    public OrderStatsResponse getOrderStats() {
        // 空实现或可以根据需要保留原始实现
        return new OrderStatsResponse();
    }
}