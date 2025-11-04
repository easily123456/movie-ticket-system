package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.UserStatsResponse;
import com.movieticket.dto.request.order.OrderCreateRequest;
import com.movieticket.dto.response.order.OrderResponse;
import com.movieticket.entity.Order;
import com.movieticket.entity.Session;
import com.movieticket.entity.User;
import com.movieticket.service.OrderService;
import com.movieticket.service.SessionService;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final SessionService sessionService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;



    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody OrderCreateRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            Optional<Session> sessionOpt = sessionService.getSessionById(request.getSessionId());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
            if (sessionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("场次不存在"));
            }

            // 检查座位是否可用
            boolean seatsAvailable = sessionService.checkSeatAvailability(request.getSessionId(), request.getSeatNumbers());
            if (!seatsAvailable) {
                return ResponseEntity.badRequest().body(ApiResponse.error("座位已被预订或不可用"));
            }

            User user = userOpt.get();
            Session session = sessionOpt.get();

            // 创建订单
            Order order = new Order();
            order.setUser(user);
            order.setSession(session);
            order.setSeatNumbers(objectMapper.writeValueAsString(request.getSeatNumbers()));
            //objectMapper.writeValueAsString(request.getSeatNumbers()) 的作用是 将 List<String> 类型的座位号列表转换为 JSON 格式的字符串
            order.setSeatCount(request.getSeatNumbers().size());
            order.setTotalPrice(session.getPrice().multiply(new BigDecimal(request.getSeatNumbers().size())));
            // 计算总价，就是场次价格乘以座位数量
            //request.getSeatNumbers().size()的返回值为int类型，为了同一类型，进行转换

            Order createdOrder = orderService.createOrder(order);
            OrderResponse response = convertToOrderResponse(createdOrder);

            return ResponseEntity.ok(ApiResponse.success("订单创建成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建订单失败"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getOrderStats(
            @RequestHeader("Authorization") String token) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            // 获取用户订单统计
            UserStatsResponse stats = userService.getUserStats(userId);

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单统计失败"));
        }
    }
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getUserOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Pageable pageable = PageRequest.of(page, size);//PageRequest是Pageable的实现类
            Page<Order> orders = orderService.getOrdersByUser(userId, pageable);
            Page<OrderResponse> response = orders.map(this::convertToOrderResponse);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单失败"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<Order> orderOpt = orderService.getOrderById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // 检查订单是否属于当前用户
                if (!order.getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("无权访问此订单"));
                }
                //验证token中的id与请求的id一致

                OrderResponse response = convertToOrderResponse(order);
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单失败"));
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Void>> payOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<Order> orderOpt = orderService.getOrderById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // 检查订单是否属于当前用户
                if (!order.getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("无权操作此订单"));
                }
                


                orderService.payOrder(id);
                return ResponseEntity.ok(ApiResponse.success("支付成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单不存在"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("支付失败"));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<Order> orderOpt = orderService.getOrderById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // 检查订单是否属于当前用户
                if (!order.getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("无权操作此订单"));
                }
                // 检查订单是否已经支付
                if (order.getStatus() == Order.OrderStatus.CANCELLED) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("订单已取消"));
                }
                orderService.cancelOrder(id);
                return ResponseEntity.ok(ApiResponse.success("取消成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单不存在"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消失败"));
        }
    }

    private OrderResponse convertToOrderResponse(Order order) {
        try {
            OrderResponse response = new OrderResponse(order);

            List<String> seatNumbers = objectMapper.readValue(order.getSeatNumbers(), new TypeReference<List<String>>() {});
            String seatNumbersString = String.join(",", seatNumbers);
            response.setSeatNumbers(seatNumbersString);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("转换订单响应失败", e);
        }
    }
}