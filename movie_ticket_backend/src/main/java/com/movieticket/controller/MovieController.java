package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.entity.Movie;
import com.movieticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMovies(
            @RequestParam(defaultValue = "0") int page, //指定查询的页码（从 0 开始）
            @RequestParam(defaultValue = "10") int size, //指定每页的记录数10
            @RequestParam(defaultValue = "createTime") String sort, //指定排序字段
            @RequestParam(defaultValue = "desc") String direction) {//指定排序方向
        //@RequestParam为 HTTP 请求的 查询参数（Query Parameters） 或 表单数据（Form Data） 中提取参数值，并将其绑定到控制器方法的参数上

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        //Sort.Direction为枚举类，用于指定排序方向，可以是 ASC（升序） 或 DESC（降序）
        //direction.equalsIgnoreCase()方法用于忽略大小写，判断字符串是否相等

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        //PageRequest是Pageable的实现类，用于创建分页信息
        //of()方法用于创建Pageable对象，Sort.by(sortDirection, sort)为排序方向和排序字段

        Page<Movie> movies = movieService.getActiveMovies(pageable);
        Page<MovieResponse> response = movies.map(this::convertToMovieResponse);//Page<Movie> 对象转换为一个 Page<MovieResponse> 对象

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getHotMovies() {
        List<Movie> movies = movieService.getHotMovies();
        List<MovieResponse> response = movies.stream()
                .map(this::convertToMovieResponse)
                .collect(Collectors.toList());//List<Movie> 转换为 List<MovieResponse>
        //List 本身没有 map() 方法
        //Stream 提供了函数式操作
        //Stream.map()会自动将流中的每个 Movie 对象作为参数传递给 convertToMovieResponse，所以不需手动传参

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getUpcomingMovies() {
        List<Movie> movies = movieService.getUpcomingMovies();
        List<MovieResponse> response = movies.stream()
                .map(this::convertToMovieResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovie(@PathVariable Long id) {
        Optional<Movie> movieOpt = movieService.getMovieById(id);
        if (movieOpt.isPresent()) {
            MovieResponse response = convertToMovieResponse(movieOpt.get());
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("电影不存在"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> searchMovies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.searchMovies(keyword, pageable);
        Page<MovieResponse> response = movies.map(this::convertToMovieResponse);
        //movies.map(this::convertToMovieResponse)为对 Page 中的每个元素进行转换，返回一个新的 Page 对象

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMoviesByGenre(
            @PathVariable Long genreId,//@PathVariable注解用于将 URL 中的变量绑定到控制器方法参数上
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getMoviesByGenre(genreId, pageable);
        Page<MovieResponse> response = movies.map(this::convertToMovieResponse);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private MovieResponse convertToMovieResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setOriginalTitle(movie.getOriginalTitle());
        response.setGenreName(movie.getGenre().getName());
        response.setGenreId(movie.getGenre().getId());
        response.setDuration(movie.getDuration());
        response.setDirector(movie.getDirector());
        response.setActors(movie.getActors());
        response.setReleaseDate(movie.getReleaseDate());
        response.setCountry(movie.getCountry());
        response.setLanguage(movie.getLanguage());
        response.setDescription(movie.getDescription());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setRating(movie.getRating());
        response.setVoteCount(movie.getVoteCount());
        response.setPrice(movie.getPrice());
        response.setIsHot(movie.getIsHot());
        response.setStatus(movie.getStatus());
        return response;
    }
}