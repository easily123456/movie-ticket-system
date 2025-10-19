package com.movieticket.repository;

import com.movieticket.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}