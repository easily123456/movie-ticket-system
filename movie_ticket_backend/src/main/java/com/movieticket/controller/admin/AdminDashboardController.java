package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.UserActivityResponse;
import com.movieticket.dto.UserStatsResponse;
import com.movieticket.dto.response.admin.DashboardStatsResponse;
import com.movieticket.dto.response.admin.RevenueDataResponse;
import com.movieticket.dto.response.admin.OrderStatsResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.dto.response.order.OrderResponse;
import com.movieticket.entity.Movie;
import com.movieticket.entity.Order;
import com.movieticket.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final MovieService movieService;
    private final OrderService orderService;
    private final NewsService newsService;
    private final SessionService sessionService;
    private final DashboardService dashboardService;

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
        stats.setCancelledOrders(
                orderService.getOrderCountByStatus(com.movieticket.entity.Order.OrderStatus.CANCELLED));
        // stats.setCancelledOrders(orderService.getOrderCountByStatus(Order.OrderStatus.CANCELLED));
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

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueDataResponse>> getRevenueData(
            @RequestParam(defaultValue = "week") String range) {
        try {
            RevenueDataResponse data = dashboardService.getRevenueData(range);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取收入数据失败"));
        }
    }

    @GetMapping("/order-stats")
    public ResponseEntity<ApiResponse<OrderStatsResponse>> getOrderStats() {
        try {
            OrderStatsResponse stats = dashboardService.getOrderStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单统计失败"));
        }
    }

    @GetMapping("/hot-movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getHotMovies(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // MovieService already provides DTO-style hot movie list
            List<MovieResponse> response = movieService.getHotMovies(limit);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取热门电影失败"));
        }
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRecentOrders() {
        try {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Order> orders = orderService.getAllOrders(pageable);
            List<OrderResponse> response = orders.getContent().stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取最近订单失败"));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteUsers(@RequestBody List<Long> userIds) {
        try {
            userService.batchDeleteUsers(userIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/disable")
    public ResponseEntity<ApiResponse<Void>> batchDisableUsers(@RequestBody List<Long> userIds) {
        try {
            userService.batchChangeUserStatus(userIds, false);
            return ResponseEntity.ok(ApiResponse.success("批量禁用成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/enable")
    public ResponseEntity<ApiResponse<Void>> batchEnableUsers(@RequestBody List<Long> userIds) {
        try {
            userService.batchChangeUserStatus(userIds, true);
            return ResponseEntity.ok(ApiResponse.success("批量启用成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(@PathVariable Long id) {
        try {
            UserStatsResponse stats = userService.getUserStats(id);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivities(@PathVariable Long id) {
        try {
            List<UserActivityResponse> activities = userService.getUserActivities(id);
            return ResponseEntity.ok(ApiResponse.success(activities));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 辅助方法用于转换Movie到MovieResponse
    private MovieResponse convertToMovieResponse(Movie movie) {
        return new MovieResponse(movie);
    }

    // 辅助方法用于转换Order到OrderResponse
    private OrderResponse convertToOrderResponse(Order order) {
        return new OrderResponse(order);
    }
}