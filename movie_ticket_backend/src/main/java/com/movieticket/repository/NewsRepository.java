package com.movieticket.repository;

import com.movieticket.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByStatusTrueAndIsTopTrueOrderByPublishTimeDesc();

    Page<News> findByStatusTrueOrderByPublishTimeDesc(Pageable pageable);

    Page<News> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.status = true AND " +
            "(n.title LIKE %:keyword% OR n.content LIKE %:keyword% OR n.author LIKE %:keyword%)")
    Page<News> searchNews(String keyword, Pageable pageable);

    @Query("SELECT COUNT(n) FROM News n WHERE n.status = true")
    long countActiveNews();

    // 管理端查询方法
    Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<News> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

//    Page<News> findByStatus(Boolean status, Pageable pageable);

    Page<News> findByIsTop(Boolean isTop, Pageable pageable);

    Page<News> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 前端查询方法
    List<News> findByStatusOrderByIsTopDescPublishTimeDesc(Boolean status);

    List<News> findByIsTopAndStatusOrderByPublishTimeDesc(Boolean isTop, Boolean status);

    @Modifying
    @Query("UPDATE News n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void incrementViewCount(@Param("id") Long id);

    Long countByStatus(boolean status);
}