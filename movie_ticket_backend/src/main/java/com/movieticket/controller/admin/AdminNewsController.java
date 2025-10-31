package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.News;
import com.movieticket.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<News>>> getNewsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer isTop,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<News> news;
        if (title != null && !title.trim().isEmpty()) {
            news = newsService.searchNewsByTitle(title, pageable);
        } else if (author != null && !author.trim().isEmpty()) {
            news = newsService.searchNewsByAuthor(author, pageable);
        } else if (status != null) {
            news = newsService.getNewsByStatus(status, pageable);
        } else if (isTop != null) {
            news = newsService.getNewsByTopStatus(isTop, pageable);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            news = newsService.getNewsByDateRange(startDateTime, endDateTime, pageable);
        } else {
            news = newsService.getAllNews(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<News>> getNewsDetail(@PathVariable Long id) {
        Optional<News> newsOpt = newsService.getNewsById(id);
        if (newsOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(newsOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("资讯不存在"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<News>> createNews(@RequestBody News news) {
        try {
            News createdNews = newsService.createNews(news);
            return ResponseEntity.ok(ApiResponse.success("资讯创建成功", createdNews));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<News>> updateNews(@PathVariable Long id, @RequestBody News news) {
        try {
            news.setId(id);
            News updatedNews = newsService.updateNews(news);
            return ResponseEntity.ok(ApiResponse.success("资讯更新成功", updatedNews));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<News>> publishNews(@PathVariable Long id) {
        try {
            News news = newsService.publishNews(id);
            return ResponseEntity.ok(ApiResponse.success("资讯发布成功", news));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<News>> unpublishNews(@PathVariable Long id) {
        try {
            News news = newsService.unpublishNews(id);
            return ResponseEntity.ok(ApiResponse.success("资讯取消发布成功", news));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/top")
    public ResponseEntity<ApiResponse<News>> setNewsTop(
            @PathVariable Long id,
            @RequestParam Boolean isTop) {
        try {
            News news = newsService.setNewsTop(id, isTop);
            String message = isTop ? "资讯置顶成功" : "资讯取消置顶成功";
            return ResponseEntity.ok(ApiResponse.success(message, news));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.ok(ApiResponse.success("资讯删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteNews(@RequestBody List<Long> newsIds) {
        try {
            newsService.batchDeleteNews(newsIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/publish")
    public ResponseEntity<ApiResponse<Void>> batchPublishNews(@RequestBody List<Long> newsIds) {
        try {
            newsService.batchPublishNews(newsIds);
            return ResponseEntity.ok(ApiResponse.success("批量发布成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/top")
    public ResponseEntity<ApiResponse<Void>> batchSetNewsTop(
            @RequestBody BatchNewsTopRequest request) {
        try {
            newsService.batchSetNewsTop(request.getNewsIds(), request.getIsTop());
            String message = request.getIsTop() ? "批量置顶成功" : "批量取消置顶成功";
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 这里实现文件上传逻辑
            // String fileUrl = fileService.uploadFile(file);

            // 暂时返回模拟数据
            UploadResponse response = new UploadResponse();
            response.setUrl("/uploads/" + file.getOriginalFilename());
            response.setName(file.getOriginalFilename());
            response.setSize(file.getSize());

            return ResponseEntity.ok(ApiResponse.success("上传成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传失败"));
        }
    }

    // 请求和响应DTO
    public static class BatchNewsTopRequest {
        private List<Long> newsIds;
        private Boolean isTop;

        // getters and setters
        public List<Long> getNewsIds() { return newsIds; }
        public void setNewsIds(List<Long> newsIds) { this.newsIds = newsIds; }
        public Boolean getIsTop() { return isTop; }
        public void setIsTop(Boolean isTop) { this.isTop = isTop; }
    }

    public static class UploadResponse {
        private String url;
        private String name;
        private Long size;

        // getters and setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
    }
}