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
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.movie.id = :movieId")
    long countByMovieId(@Param("movieId") Long movieId);



    Page<Session> findByMovieTitleContainingIgnoreCase(String movieTitle, Pageable pageable);

    Page<Session> findByHallId(Long hallId, Pageable pageable);

    Page<Session> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);


    @Query("SELECT s FROM Session s WHERE " +
            "s.hall.id = :hallId AND " +
            "s.status = true AND " +
            "((s.startTime BETWEEN :startTime AND :endTime) OR " +
            "(s.endTime BETWEEN :startTime AND :endTime) OR " +
            "(s.startTime <= :startTime AND s.endTime >= :endTime)) AND " +
            "(:excludeId IS NULL OR s.id != :excludeId)")
    List<Session> findConflictingSessions(
            @Param("hallId") Long hallId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId);



    // 获取电影的未来场次
    @Query("SELECT s FROM Session s WHERE s.movie.id = :movieId AND s.startTime > :now AND s.status = true ORDER BY s.startTime ASC")
    List<Session> findUpcomingSessionsByMovie(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    // 获取指定日期的场次
    @Query("SELECT s FROM Session s WHERE DATE(s.startTime) = DATE(:date) AND s.status = true ORDER BY s.startTime ASC")
    List<Session> findSessionsByDate(@Param("date") LocalDateTime date);

    // 获取热门场次（预订量高的）
    @Query("SELECT s FROM Session s WHERE s.startTime > :now AND s.status = true ORDER BY s.bookedSeats DESC")
    Page<Session> findPopularSessions(@Param("now") LocalDateTime now, Pageable pageable);

    Long countByStartTimeAfterAndStatus(LocalDateTime now, boolean b);
}