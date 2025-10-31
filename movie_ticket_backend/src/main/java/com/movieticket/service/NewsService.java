package com.movieticket.service;

import com.movieticket.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsService {
    News createNews(News news);
    News updateNews(News news);
    void deleteNews(Long id);
    Optional<News> getNewsById(Long id);
    List<News> getTopNews();
    Page<News> getAllActiveNews(Pageable pageable);
    Page<News> getAllNews(Pageable pageable);
    Page<News> searchNews(String keyword, Pageable pageable);
    News changeNewsStatus(Long id, Boolean status);
    News toggleTopNews(Long id, Boolean isTop);
    void incrementViewCount(Long id);
    long getActiveNewsCount();


    // 管理员查询方法
    Page<News> searchNewsByTitle(String title, Pageable pageable);
    Page<News> searchNewsByAuthor(String author, Pageable pageable);
    Page<News> getNewsByStatus(Integer status, Pageable pageable);
    Page<News> getNewsByTopStatus(Integer isTop, Pageable pageable);
    Page<News> getNewsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    News publishNews(Long id);
    News unpublishNews(Long id);
    News setNewsTop(Long id, Boolean isTop);
    void batchPublishNews(List<Long> newsIds);
    void batchSetNewsTop(List<Long> newsIds, Boolean isTop);
    void batchDeleteNews(List<Long> newsIds);
}