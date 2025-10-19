package com.movieticket.service.impl;

import com.movieticket.entity.News;
import com.movieticket.repository.NewsRepository;
import com.movieticket.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public News createNews(News news) {
        if (news.getPublishTime() == null) {
            news.setPublishTime(LocalDateTime.now());
        }
        news.setStatus(true);
        return newsRepository.save(news);
    }

    @Override
    public News updateNews(News news) {
        News existingNews = newsRepository.findById(news.getId())
                .orElseThrow(() -> new RuntimeException("资讯不存在"));

        existingNews.setTitle(news.getTitle());
        existingNews.setContent(news.getContent());
        existingNews.setCoverImage(news.getCoverImage());
        existingNews.setAuthor(news.getAuthor());
        existingNews.setPublishTime(news.getPublishTime());

        return newsRepository.save(existingNews);
    }

    @Override
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资讯不存在"));
        newsRepository.delete(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<News> getNewsById(Long id) {
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            // 增加浏览次数
            incrementViewCount(id);
        }
        return news;
    }

    @Override
    @Transactional(readOnly = true)
    public List<News> getTopNews() {
        return newsRepository.findByStatusTrueAndIsTopTrueOrderByPublishTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getAllActiveNews(Pageable pageable) {
        return newsRepository.findByStatusTrueOrderByPublishTimeDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> searchNews(String keyword, Pageable pageable) {
        return newsRepository.searchNews(keyword, pageable);
    }

    @Override
    public News changeNewsStatus(Long id, Boolean status) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资讯不存在"));
        news.setStatus(status);
        return newsRepository.save(news);
    }

    @Override
    public News toggleTopNews(Long id, Boolean isTop) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资讯不存在"));
        news.setIsTop(isTop);
        return newsRepository.save(news);
    }

    @Override
    public void incrementViewCount(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资讯不存在"));
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveNewsCount() {
        return newsRepository.countActiveNews();
    }
}