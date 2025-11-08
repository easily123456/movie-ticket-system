package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.user.PasswordChangeRequest;
import com.movieticket.dto.request.user.UserUpdateRequest;
import com.movieticket.dto.response.user.UserProfileResponse;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// 添加需要的导入
import com.movieticket.service.OrderService;
import com.movieticket.service.FavoriteService;
import com.movieticket.service.CommentService;
import com.movieticket.entity.Order;
import com.movieticket.entity.Favorite;
import com.movieticket.entity.Comment;
import com.movieticket.dto.response.order.OrderResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.dto.response.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    // 添加新的服务依赖
    private final OrderService orderService;
    private final FavoriteService favoriteService;
    private final CommentService commentService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@RequestHeader("Authorization") String token) {// 获取用户信息
        // @RequestHeader("Authorization")从 HTTP 请求头中获取名为 Authorization 的值，并自动绑定到方法的参数
        // token 上
        // token格式为：Bearer <TOKEN>，机制为JWT

        // Authorization: Bearer <TOKEN>
        try {
            String authToken = token.substring(7); // 去掉 "Bearer " 前缀
            Long userId = jwtUtil.getUserIdFromToken(authToken);// 获取用户ID

            Optional<User> userOpt = userService.getUserById(userId);// 根据ID获取用户
            if (userOpt.isPresent()) {
                UserProfileResponse response = new UserProfileResponse(userOpt.get());
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setAvatar(request.getAvatar());

                User updatedUser = userService.updateUser(user);
                UserProfileResponse response = new UserProfileResponse(updatedUser);
                return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (RuntimeException e) {// 可能存在用户名称重复，因此先被具体的错误处理
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 验证原密码
                if (!userService.login(user.getUsername(), request.getOldPassword()).isPresent()) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("原密码错误"));
                }

                // 从请求体中获取新密码，并将其设置给用户对象
                // user.setPassword(request.getNewPassword());
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userService.updateUser(user);

                return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token无效"));
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders;
            if (status != null && !status.isBlank()) {
                try {
                    Order.OrderStatus os = Order.OrderStatus.valueOf(status);
                    orders = orderService.getOrdersByUserAndStatus(userId, os, pageable);
                } catch (IllegalArgumentException ex) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("无效的订单状态"));
                }
            } else {
                orders = orderService.getOrdersByUser(userId, pageable);
            }
            Page<OrderResponse> response = orders.map(this::convertToOrderResponse);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单失败"));
        }
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getFavorites(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Pageable pageable = PageRequest.of(page, size);
            Page<Favorite> favorites = favoriteService.getFavoritesByUser(userId, pageable);
            Page<MovieResponse> response = favorites.map(favorite -> convertToMovieResponse(favorite.getMovie()));

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取收藏失败"));
        }
    }

    /**
     * 获取用户评论列表
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> comments = commentService.getCommentsByUser(userId, pageable);
            Page<CommentResponse> response = comments.map(this::convertToCommentResponse);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取评论失败"));
        }
    }

    /**
     * 转换订单实体为响应DTO
     */
    private OrderResponse convertToOrderResponse(Order order) {
        return new OrderResponse(order);
    }

    /**
     * 转换电影实体为响应DTO
     */
    private MovieResponse convertToMovieResponse(com.movieticket.entity.Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setOriginalTitle(movie.getOriginalTitle());
        if (movie.getGenre() != null) {
            response.setGenreName(movie.getGenre().getName());
            response.setGenreId(movie.getGenre().getId());
        }
        response.setDuration(movie.getDuration());
        response.setDirector(movie.getDirector());
        response.setActors(movie.getActors());
        response.setReleaseDate(movie.getReleaseDate());
        response.setCountry(movie.getCountry());
        response.setLanguage(movie.getLanguage());
        response.setDescription(movie.getDescription());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setRating(movie.getRating());
        response.setVoteCount(movie.getVoteCount());
        response.setPrice(movie.getPrice());
        response.setIsHot(movie.getIsHot());
        response.setStatus(movie.getStatus());
        return response;
    }

    /**
     * 转换评论实体为响应DTO
     */
    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setAvatar(comment.getUser().getAvatar());
        response.setMovieId(comment.getMovie().getId());
        response.setContent(comment.getContent());
        response.setRating(comment.getRating());
        response.setLikeCount(comment.getLikeCount());
        response.setCreateTime(comment.getCreateTime());
        return response;
    }
}