package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.request.movie.MovieQueryRequest;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.entity.Genre;
import com.movieticket.service.GenreService;
import com.movieticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMovies(
            MovieQueryRequest request) {
        
        try {
            Page<MovieResponse> movies = movieService.getMovies(request);
            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取电影列表失败"));
        }
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getHotMovies(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<MovieResponse> movies = movieService.getHotMovies(limit);
            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取热门电影失败"));
        }
    }

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getNewMovies(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<MovieResponse> movies = movieService.getNewMovies(limit);
            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取最新电影失败"));
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getTopRatedMovies(
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<MovieResponse> movies = movieService.getTopRatedMovies(limit);
            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取高分电影失败"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> searchMovies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Page<MovieResponse> movies = movieService.searchMovies(keyword, 
                org.springframework.data.domain.PageRequest.of(page, size));
            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索电影失败"));
        }
    }

    @GetMapping("/by-genre/{genreId}")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            if (!genreService.existsById(genreId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("电影类型不存在"));
            }

            Page<MovieResponse> movies = movieService.getMoviesByGenre(genreId, 
                org.springframework.data.domain.PageRequest.of(page, size));

            return ResponseEntity.ok(ApiResponse.success(movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取类型电影失败"));
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovie(@PathVariable Long id) {
        try {
            Optional<MovieResponse> movie = movieService.getMovieDetail(id);
            if (movie.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(movie.get()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("电影不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取电影详情失败"));
        }
    }


}