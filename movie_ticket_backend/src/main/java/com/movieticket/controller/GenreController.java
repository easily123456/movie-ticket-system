package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Genre;
import com.movieticket.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getMovieGenres() {
        try {
            List<Genre> genres = genreService.getAllActiveGenres();
            return ResponseEntity.ok(ApiResponse.success(genres));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取电影类型失败"));
        }
    }
}
