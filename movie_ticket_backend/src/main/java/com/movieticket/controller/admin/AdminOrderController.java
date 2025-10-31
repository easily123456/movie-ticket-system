package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Order;
import com.movieticket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String movieTitle,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Order> orders;
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            orders = orderService.searchOrdersByOrderNo(orderNo, pageable);
        } else if (username != null && !username.trim().isEmpty()) {
            orders = orderService.searchOrdersByUsername(username, pageable);
        } else if (movieTitle != null && !movieTitle.trim().isEmpty()) {
            orders = orderService.searchOrdersByMovieTitle(movieTitle, pageable);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            orders = orderService.getOrdersByDateRange(startDateTime, endDateTime, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(orderOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("订单不存在"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("订单状态更新成功", order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.success("订单删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteOrders(@RequestBody List<Long> orderIds) {
        try {
            orderService.batchDeleteOrders(orderIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Void>> batchUpdateOrderStatus(
            @RequestBody BatchOrderStatusRequest request) {
        try {
            orderService.batchUpdateOrderStatus(request.getOrderIds(), request.getStatus());
            return ResponseEntity.ok(ApiResponse.success("批量更新成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<OrderStatsResponse>> getOrderStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            OrderStatsResponse stats = new OrderStatsResponse();

            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

            // 获取订单统计信息
            if (startDateTime != null && endDateTime != null) {
                stats.setTotalOrders(orderService.getOrderCountByDateRange(startDateTime, endDateTime));
                stats.setPendingOrders(orderService.getOrderCountByStatusAndDateRange(Order.OrderStatus.PENDING, startDateTime, endDateTime));
                stats.setPaidOrders(orderService.getOrderCountByStatusAndDateRange(Order.OrderStatus.PAID, startDateTime, endDateTime));
                stats.setCancelledOrders(orderService.getOrderCountByStatusAndDateRange(Order.OrderStatus.CANCELLED, startDateTime, endDateTime));
                stats.setRefundedOrders(orderService.getOrderCountByStatusAndDateRange(Order.OrderStatus.REFUNDED, startDateTime, endDateTime));
            } else {
                stats.setTotalOrders(orderService.getOrderCountByStatus(null));
                stats.setPendingOrders(orderService.getOrderCountByStatus(Order.OrderStatus.PENDING));
                stats.setPaidOrders(orderService.getOrderCountByStatus(Order.OrderStatus.PAID));
                stats.setCancelledOrders(orderService.getOrderCountByStatus(Order.OrderStatus.CANCELLED));
                stats.setRefundedOrders(orderService.getOrderCountByStatus(Order.OrderStatus.REFUNDED));
            }

            // 获取收入统计
            stats.setTotalRevenue(orderService.getTotalRevenue(startDateTime, endDateTime));
            stats.setTodayRevenue(orderService.getTodayRevenue());
            stats.setMonthlyRevenue(orderService.getMonthlyRevenue());

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单统计失败"));
        }
    }

    @PostMapping("/export")
    public ResponseEntity<?> exportOrders(@RequestBody OrderExportRequest request) {
        try {
            // 这里实现订单导出逻辑
            // 返回Excel文件流
            // 暂时返回成功响应
            return ResponseEntity.ok(ApiResponse.success("导出任务已开始"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("导出失败"));
        }
    }

    // 请求和响应DTO
    public static class BatchOrderStatusRequest {
        private List<Long> orderIds;
        private Order.OrderStatus status;

        // getters and setters
        public List<Long> getOrderIds() { return orderIds; }
        public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }
        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
    }

    public static class OrderStatsResponse {
        private Long totalOrders;
        private Long pendingOrders;
        private Long paidOrders;
        private Long cancelledOrders;
        private Long refundedOrders;
        private Double totalRevenue;
        private Double todayRevenue;
        private Double monthlyRevenue;

        // getters and setters
        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public Long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Long pendingOrders) { this.pendingOrders = pendingOrders; }
        public Long getPaidOrders() { return paidOrders; }
        public void setPaidOrders(Long paidOrders) { this.paidOrders = paidOrders; }
        public Long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(Long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        public Long getRefundedOrders() { return refundedOrders; }
        public void setRefundedOrders(Long refundedOrders) { this.refundedOrders = refundedOrders; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Double getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(Double todayRevenue) { this.todayRevenue = todayRevenue; }
        public Double getMonthlyRevenue() { return monthlyRevenue; }
        public void setMonthlyRevenue(Double monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    }

    public static class OrderExportRequest {
        private String orderNo;
        private String username;
        private String movieTitle;
        private Order.OrderStatus status;
        private LocalDate startDate;
        private LocalDate endDate;

        // getters and setters
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}