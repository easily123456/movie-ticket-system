package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.comment.CommentCreateRequest;
import com.movieticket.dto.response.comment.CommentResponse;
import com.movieticket.entity.Comment;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import com.movieticket.service.CommentService;
import com.movieticket.service.MovieService;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final MovieService movieService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CommentCreateRequest request) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            Optional<Movie> movieOpt = movieService.getMovieById(request.getMovieId());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
            if (movieOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("电影不存在"));
            }

            User user = userOpt.get();
            Movie movie = movieOpt.get();

            Comment comment = new Comment();
            comment.setUser(user);
            comment.setMovie(movie);
            comment.setContent(request.getContent());
            comment.setRating(request.getRating());

            Comment createdComment = commentService.createComment(comment);
            CommentResponse response = convertToCommentResponse(createdComment);

            return ResponseEntity.ok(ApiResponse.success("评论成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("评论失败"));
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getMovieComments(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getCommentsByMovie(movieId, pageable);
        Page<CommentResponse> response = comments.map(this::convertToCommentResponse);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getUserComments(
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<Comment> commentOpt = commentService.getCommentById(id);
            if (commentOpt.isPresent()) {
                Comment comment = commentOpt.get();
                // 检查评论是否属于当前用户
                if (!comment.getUser().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("无权删除此评论"));
                }

                commentService.deleteComment(id);
                return ResponseEntity.ok(ApiResponse.success("删除成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("评论不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除失败"));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likeComment(@PathVariable Long id) {
        try {
            commentService.likeComment(id);
            return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("点赞失败"));
        }
    }

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