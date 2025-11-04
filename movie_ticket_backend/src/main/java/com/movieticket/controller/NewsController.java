package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.News;
import com.movieticket.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 获取新闻列表（仅已发布且按置顶和发布时间排序）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<News>>> getNewsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishTime") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<News> news = newsService.getAllActiveNews(pageable);
        return ResponseEntity.ok(ApiResponse.success(news));
    }

    /**
     * 获取新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<News>> getNewsDetail(@PathVariable Long id) {
        Optional<News> newsOpt = newsService.getNewsById(id);
        if (newsOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(newsOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("资讯不存在"));
        }
    }

    /**
     * 获取置顶新闻
     */
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<News>>> getTopNews() {
        List<News> topNews = newsService.getTopNews();
        return ResponseEntity.ok(ApiResponse.success(topNews));
    }
}