package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.response.admin.DashboardStatsResponse;
import com.movieticket.entity.Order;
import com.movieticket.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final MovieService movieService;
    private final OrderService orderService;
    private final NewsService newsService;
    private final SessionService sessionService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // 用户统计
        stats.setTotalUsers(userService.getTotalUserCount());
        stats.setActiveUsers(userService.getActiveUserCount());

        // 电影统计
        stats.setTotalMovies(movieService.getTotalMovieCount());
        stats.setActiveMovies(movieService.getActiveMovieCount());

        // 订单统计
        stats.setTotalOrders(orderService.getOrderCountByStatus(null));
        stats.setPendingOrders(orderService.getOrderCountByStatus(com.movieticket.entity.Order.OrderStatus.PENDING));
        stats.setPaidOrders(orderService.getOrderCountByStatus(com.movieticket.entity.Order.OrderStatus.PAID));
        stats.setCancelledOrders(orderService.getOrderCountByStatus(com.movieticket.entity.Order.OrderStatus.CANCELLED));
//        stats.setCancelledOrders(orderService.getOrderCountByStatus(Order.OrderStatus.CANCELLED));
        // 场次统计
        stats.setUpcomingSessions(sessionService.getUpcomingSessionCount());

        // 资讯统计
        stats.setTotalNews(newsService.getActiveNewsCount());

        // 营收统计
        // 获取当天的开始时间（00:00:00）
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        // 获取当天的结束时间（23:59:59）
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        stats.setTodayRevenue(orderService.getRevenueBetween(startOfDay, endOfDay));

        // 获取当前月份的第一天凌晨时间作为月度统计的起始时间
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        // 查询从月初到今日结束的收入数据，并设置到月度收入统计中
        stats.setMonthlyRevenue(orderService.getRevenueBetween(startOfMonth, endOfDay));


        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}