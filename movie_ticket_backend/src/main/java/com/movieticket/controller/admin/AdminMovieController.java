package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.response.admin.MovieStatsResponse;
import com.movieticket.entity.Movie;
import com.movieticket.dto.response.movie.MovieResponse;
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

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Boolean isHot) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<MovieResponse> movies;
        if (keyword != null && !keyword.trim().isEmpty()) {
            movies = movieService.searchMovies(keyword, pageable);
        } else if (genreId != null) {
            movies = movieService.getMoviesByGenre(genreId, pageable);
        } else if (status != null) {
            // use DTO listing via existing service
            movies = movieService.getMovies(new com.movieticket.dto.request.movie.MovieQueryRequest() {
                {
                    setStatus(status);
                    setPage(page);
                    setSize(size);
                    setSortBy(sort);
                    setSortOrder(direction);
                }
            });
        } else if (isHot != null) {
            // filter hot via query request
            movies = movieService.getMovies(new com.movieticket.dto.request.movie.MovieQueryRequest() {
                {
                    setIsHot(isHot);
                    setPage(page);
                    setSize(size);
                    setSortBy(sort);
                    setSortOrder(direction);
                }
            });
        } else {
            movies = movieService.getMovies(new com.movieticket.dto.request.movie.MovieQueryRequest() {
                {
                    setPage(page);
                    setSize(size);
                    setSortBy(sort);
                    setSortOrder(direction);
                }
            });
        }

        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Movie>> createMovie(@RequestBody Movie movie) {
        try {
            Movie createdMovie = movieService.createMovie(movie);
            return ResponseEntity.ok(ApiResponse.success("电影创建成功", createdMovie));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            movie.setId(id);
            Movie updatedMovie = movieService.updateMovie(movie);
            return ResponseEntity.ok(ApiResponse.success("电影更新成功", updatedMovie));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Movie>> changeMovieStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {
        try {
            Movie movie = movieService.changeMovieStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", movie));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/hot")
    public ResponseEntity<ApiResponse<Movie>> setMovieHot(
            @PathVariable Long id,
            @RequestParam Boolean isHot) {
        try {
            Movie movie = movieService.setMovieHot(id, isHot);
            return ResponseEntity.ok(ApiResponse.success("热门设置成功", movie));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(ApiResponse.success("电影删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteMovies(@RequestBody List<Long> movieIds) {
        try {
            movieService.batchDeleteMovies(movieIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/disable")
    public ResponseEntity<ApiResponse<Void>> batchDisableMovies(@RequestBody List<Long> movieIds) {
        try {
            movieService.batchChangeMovieStatus(movieIds, false);
            return ResponseEntity.ok(ApiResponse.success("批量下架成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/enable")
    public ResponseEntity<ApiResponse<Void>> batchEnableMovies(@RequestBody List<Long> movieIds) {
        try {
            movieService.batchChangeMovieStatus(movieIds, true);
            return ResponseEntity.ok(ApiResponse.success("批量上架成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/hot")
    public ResponseEntity<ApiResponse<Void>> batchSetMoviesHot(
            @RequestBody List<Long> movieIds,
            @RequestParam Boolean isHot) {
        try {
            movieService.batchSetMoviesHot(movieIds, isHot);
            return ResponseEntity.ok(ApiResponse.success("批量设置热门成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovie(@PathVariable Long id) {
        Optional<Movie> movieOpt = movieService.getMovieById(id);
        if (movieOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(movieOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("电影不存在"));
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<MovieStatsResponse>> getMovieStats(@PathVariable Long id) {
        try {
            MovieStatsResponse stats = movieService.getMovieStats(id);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}