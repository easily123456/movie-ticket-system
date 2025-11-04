package com.movieticket.service;

import com.movieticket.dto.request.movie.MovieQueryRequest;
import com.movieticket.dto.response.admin.MovieStatsResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.entity.Movie;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    // 获取电影列表（带筛选和分页）
    Page<MovieResponse> getMovies(MovieQueryRequest request);

    // 构建查询条件

    Specification<Movie> buildSpecification(MovieQueryRequest request);


    Pageable buildPageable(MovieQueryRequest request);

    List<MovieResponse> getHotMovies(int limit);


    List<MovieResponse> getNewMovies(int limit);

    List<MovieResponse> getTopRatedMovies(int limit);


    Page<MovieResponse> searchMovies(String keyword, Pageable pageable);

    Page<MovieResponse> getMoviesByGenre(Long genreId, Pageable pageable);


    Optional<MovieResponse> getMovieDetail(Long id);



    MovieResponse convertToResponse(Movie movie);


    String formatDuration(Integer duration);


    String formatRating(BigDecimal rating);


    Boolean isNewMovie(LocalDate releaseDate);

    Optional<Movie> getMovieById(Long movieId);

    void updateMovieRating(Long movieId);

    long getTotalMovieCount();

    long getActiveMovieCount();

    Movie createMovie(Movie movie);

    Movie updateMovie(Movie movie);

    Movie changeMovieStatus(Long id, Boolean status);

    Movie setMovieHot(Long id, Boolean isHot);

    void deleteMovie(Long id);

    void batchDeleteMovies(List<Long> movieIds);

    void batchChangeMovieStatus(List<Long> movieIds, boolean status);

    void batchSetMoviesHot(List<Long> movieIds, Boolean isHot);

    MovieStatsResponse getMovieStats(Long id);
}