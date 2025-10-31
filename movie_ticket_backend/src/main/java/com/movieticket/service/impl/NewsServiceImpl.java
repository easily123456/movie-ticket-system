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



    @Override
    @Transactional(readOnly = true)
    public Page<News> searchNewsByTitle(String title, Pageable pageable) {
        return newsRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> searchNewsByAuthor(String author, Pageable pageable) {
        return newsRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getNewsByStatus(Integer status, Pageable pageable) {
        return newsRepository.findByStatus(status == 1 ? true : false, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getNewsByTopStatus(Integer isTop, Pageable pageable) {
        return newsRepository.findByIsTop(isTop == 1 ? true : false, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getNewsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return newsRepository.findByCreateTimeBetween(start, end, pageable);
    }

    @Override
    public News setNewsTop(Long id, Boolean isTop) {
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            throw new RuntimeException("资讯不存在");
        }

        News news = newsOpt.get();
        news.setIsTop(isTop ? true : false);
        news.setUpdateTime(LocalDateTime.now());

        return newsRepository.save(news);
    }

    @Override
    public void batchDeleteNews(List<Long> newsIds) {
        newsRepository.deleteAllById(newsIds);
    }

    @Override
    public void batchPublishNews(List<Long> newsIds) {
        List<News> newsList = newsRepository.findAllById(newsIds);
        for (News news : newsList) {
            news.setStatus(true);
            if (news.getPublishTime() == null) {
                news.setPublishTime(LocalDateTime.now());
            }
            news.setUpdateTime(LocalDateTime.now());
        }
        newsRepository.saveAll(newsList);
    }

    @Override
    public void batchSetNewsTop(List<Long> newsIds, Boolean isTop) {
        List<News> newsList = newsRepository.findAllById(newsIds);
        for (News news : newsList) {
            news.setIsTop(isTop ? true : false);
            news.setUpdateTime(LocalDateTime.now());
        }
        newsRepository.saveAll(newsList);
    }

    @Override
    public News publishNews(Long id) {
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            throw new RuntimeException("资讯不存在");
        }

        News news = newsOpt.get();
        news.setStatus(true);
        news.setPublishTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());

        return newsRepository.save(news);
    }

    @Override
    public News unpublishNews(Long id) {
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            throw new RuntimeException("资讯不存在");
        }

        News news = newsOpt.get();
        news.setStatus(false);
        news.setUpdateTime(LocalDateTime.now());

        return newsRepository.save(news);
    }
}