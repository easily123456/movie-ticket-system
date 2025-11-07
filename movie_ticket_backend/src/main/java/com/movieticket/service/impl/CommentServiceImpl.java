package com.movieticket.service.impl;

import com.movieticket.dto.response.comment.CommentStatsResponse;
import com.movieticket.entity.Comment;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import com.movieticket.repository.CommentRepository;
import com.movieticket.service.CommentService;
import com.movieticket.service.MovieService;
import com.movieticket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final MovieService movieService;

    @Override
    public Comment createComment(Comment comment) {
        // 验证用户和电影存在
        User user = userService.getUserById(comment.getUser().getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Movie movie = movieService.getMovieById(comment.getMovie().getId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));

        // 检查是否已经评论过
        Optional<Comment> existingComment = commentRepository.findByUserAndMovie(user, movie);
        if (existingComment.isPresent()) {
            throw new RuntimeException("您已经评论过这部电影");
        }

        comment.setUser(user);
        comment.setMovie(movie);
        comment.setStatus(true);

        Comment savedComment = commentRepository.save(comment);

        // NOTE: 不再在创建评论时自动修改 Movie 实体的评分。
        // 后端原先会调用 movieService.updateMovieRating(movie.getId())
        // 导致 movie.rating 与 movie.voteCount 被持久化更新。
        // 为保持 movie 表中评分不随用户评论自动改变，移除该调用。

        return savedComment;
    }

    @Override
    public Comment updateComment(Comment comment) {
        Comment existingComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        existingComment.setContent(comment.getContent());
        existingComment.setRating(comment.getRating());

        Comment savedComment = commentRepository.save(existingComment);

        // NOTE: 不在更新评论时更改电影评分（保持 movie 表不被自动修改）。
        return savedComment;
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        Long movieId = comment.getMovie().getId();
        commentRepository.delete(comment);

        // NOTE: 不在删除评论时更新电影评分，以避免修改 movie 表。
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByMovie(Long movieId, Pageable pageable) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        return commentRepository.findByMovieAndStatusTrueOrderByCreateTimeDesc(movie, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUser(Long userId, Pageable pageable) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return commentRepository.findByUserOrderByCreateTimeDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getTopCommentsByMovie(Long movieId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);// 获取前limit条数据，从0页开始
        return commentRepository.findTopCommentsByMovie(movieId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentByUserAndMovie(Long userId, Long movieId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        return commentRepository.findByUserAndMovie(user, movie);
    }

    @Override
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByMovie(Long movieId) {
        return commentRepository.getAverageRatingByMovie(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentCountByMovie(Long movieId) {
        return commentRepository.countByMovie(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentStatsResponse getCommentStatsByMovie(Long movieId) {
        CommentStatsResponse stats = new CommentStatsResponse();
        stats.setMovieId(movieId);
        stats.setTotalComments(commentRepository.countByMovie(movieId));
        Double averageRating = commentRepository.getAverageRatingByMovie(movieId);
        stats.setAverageRating(averageRating != null ? BigDecimal.valueOf(averageRating) : null);

        // 实现星级统计功能 (按评分范围统计)
        stats.setFiveStarCount(commentRepository.countByMovieIdAndFiveStar(movieId));
        stats.setFourStarCount(commentRepository.countByMovieIdAndFourStar(movieId));
        stats.setThreeStarCount(commentRepository.countByMovieIdAndThreeStar(movieId));
        stats.setTwoStarCount(commentRepository.countByMovieIdAndTwoStar(movieId));
        stats.setOneStarCount(commentRepository.countByMovieIdAndOneStar(movieId));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getLatestCommentsByMovie(Long movieId, int limit) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        Pageable pageable = PageRequest.of(0, limit);
        Page<Comment> page = commentRepository.findByMovieAndStatusTrueOrderByCreateTimeDesc(movie, pageable);
        return page.getContent();
    }
}