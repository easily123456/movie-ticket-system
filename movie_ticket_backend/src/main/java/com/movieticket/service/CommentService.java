package com.movieticket.service;

import com.movieticket.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment createComment(Comment comment);
    Comment updateComment(Comment comment);
    void deleteComment(Long id);
    Optional<Comment> getCommentById(Long id);
    Page<Comment> getCommentsByMovie(Long movieId, Pageable pageable);
    Page<Comment> getCommentsByUser(Long userId, Pageable pageable);
    List<Comment> getTopCommentsByMovie(Long movieId, int limit);
    Optional<Comment> getCommentByUserAndMovie(Long userId, Long movieId);
    void likeComment(Long commentId);
    Double getAverageRatingByMovie(Long movieId);
    long getCommentCountByMovie(Long movieId);
//    boolean existsByMovie(Long movieId);
}