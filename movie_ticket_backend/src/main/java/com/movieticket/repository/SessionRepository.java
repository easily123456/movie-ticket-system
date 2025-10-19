package com.movieticket.repository;

import com.movieticket.entity.Session;
import com.movieticket.entity.Movie;
import com.movieticket.entity.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByMovieAndStatusTrueOrderByStartTime(Movie movie);

    List<Session> findByStartTimeBetweenAndStatusTrue(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Session s WHERE s.movie.id = :movieId AND s.status = true AND s.startTime >= :startTime ORDER BY s.startTime")
    List<Session> findUpcomingSessionsByMovie(@Param("movieId") Long movieId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT s FROM Session s WHERE s.hall.id = :hallId AND s.status = true AND " +
            "((s.startTime BETWEEN :startTime AND :endTime) OR (s.endTime BETWEEN :startTime AND :endTime))")
    List<Session> findConflictingSessions(@Param("hallId") Long hallId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    Page<Session> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.movie.title LIKE %:keyword% OR s.hall.name LIKE %:keyword%")
    Page<Session> searchSessions(String keyword, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.status = true AND s.startTime >= :startTime")
    long countUpcomingSessions(@Param("startTime") LocalDateTime startTime);
}