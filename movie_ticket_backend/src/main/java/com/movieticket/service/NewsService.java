package com.movieticket.service;

import com.movieticket.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}