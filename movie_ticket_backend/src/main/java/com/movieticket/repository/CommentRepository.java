package com.movieticket.repository;

import com.movieticket.entity.Comment;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByMovieAndStatusTrueOrderByCreateTimeDesc(Movie movie, Pageable pageable);

    Page<Comment> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    Optional<Comment> findByUserAndMovie(User user, Movie movie);

    @Query("SELECT c FROM Comment c WHERE c.movie.id = :movieId AND c.status = true ORDER BY c.likeCount DESC, c.createTime DESC")
    List<Comment> findTopCommentsByMovie(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.movie.id = :movieId AND c.status = true")
    Double getAverageRatingByMovie(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.movie.id = :movieId AND c.status = true")
    long countByMovie(@Param("movieId") Long movieId);

    Page<Comment> findByStatus(Boolean status, Pageable pageable);
}