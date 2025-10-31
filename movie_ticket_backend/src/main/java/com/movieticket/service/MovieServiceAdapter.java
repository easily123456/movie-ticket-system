package com.movieticket.service;

import com.movieticket.dto.response.admin.MovieStatsResponse;
import com.movieticket.entity.Movie;
import com.movieticket.repository.CommentRepository;
import com.movieticket.repository.FavoriteRepository;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.OrderRepository;
import com.movieticket.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Adapter service that provides entity-level APIs expected by controllers and
 * other services.
 * It extends the existing MovieService (which contains DTO-oriented helpers) so
 * we don't
 * modify the original movie-related file. This adapter is marked @Primary so
 * Spring will
 * inject it where a MovieService bean is required.
 */
public class MovieServiceAdapter {

  private final MovieRepository movieRepository;
  private final CommentRepository commentRepository;
  private final OrderRepository orderRepository;
  private final FavoriteRepository favoriteRepository;
  private final SessionRepository sessionRepository;

  public MovieServiceAdapter(MovieRepository movieRepository,
      CommentRepository commentRepository,
      OrderRepository orderRepository,
      FavoriteRepository favoriteRepository,
      SessionRepository sessionRepository) {
    this.movieRepository = movieRepository;
    this.commentRepository = commentRepository;
    this.orderRepository = orderRepository;
    this.favoriteRepository = favoriteRepository;
    this.sessionRepository = sessionRepository;
  }

  // 实体级方法实现
  public Optional<Movie> getMovieById(Long id) {
    return movieRepository.findById(id);
  }

  public void updateMovieRating(Long movieId) {
    Double avg = commentRepository.getAverageRatingByMovie(movieId);
    long count = commentRepository.countByMovieId(movieId);
    Movie movie = movieRepository.findById(movieId).orElse(null);
    if (movie == null)
      return;
    if (avg == null) {
      movie.setRating(BigDecimal.ZERO);
    } else {
      movie.setRating(BigDecimal.valueOf(avg));
    }
    movie.setVoteCount((int) count);
    movieRepository.save(movie);
  }

  public long getTotalMovieCount() {
    return movieRepository.count();
  }

  public long getActiveMovieCount() {
    return movieRepository.countActiveMovies();
  }

  public List<Movie> getHotMovies(int limit) {
    return movieRepository.findByIsHotAndStatusOrderByRatingDesc(true, true, PageRequest.of(0, limit));
  }

  public Page<Movie> searchMovies(String keyword, Pageable pageable) {
    return movieRepository.searchMovies(keyword, pageable);
  }

  public Page<Movie> getMoviesByGenre(Long genreId, Pageable pageable) {
    return movieRepository.findByGenreIdAndStatus(genreId, true, pageable);
  }

  public Page<Movie> getMoviesByStatus(Boolean status, Pageable pageable) {
    return movieRepository.findByStatus(status, pageable);
  }

  public Page<Movie> getMoviesByHotStatus(Boolean isHot, Pageable pageable) {
    return movieRepository.findByIsHot(isHot, pageable);
  }

  public Page<Movie> getAllMovies(Pageable pageable) {
    return movieRepository.findAll(pageable);
  }

  public Movie createMovie(Movie movie) {
    return movieRepository.save(movie);
  }

  public Movie updateMovie(Movie movie) {
    return movieRepository.save(movie);
  }

  public Movie changeMovieStatus(Long id, Boolean status) {
    Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("电影不存在"));
    movie.setStatus(status);
    return movieRepository.save(movie);
  }

  public Movie setMovieHot(Long id, Boolean isHot) {
    Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("电影不存在"));
    movie.setIsHot(isHot);
    return movieRepository.save(movie);
  }

  public void deleteMovie(Long id) {
    movieRepository.deleteById(id);
  }

  public void batchDeleteMovies(List<Long> movieIds) {
    movieRepository.deleteAllById(movieIds);
  }

  public void batchChangeMovieStatus(List<Long> movieIds, boolean status) {
    movieIds.forEach(id -> {
      movieRepository.findById(id).ifPresent(m -> {
        m.setStatus(status);
        movieRepository.save(m);
      });
    });
  }

  public void batchSetMoviesHot(List<Long> movieIds, Boolean isHot) {
    movieIds.forEach(id -> {
      movieRepository.findById(id).ifPresent(m -> {
        m.setIsHot(isHot);
        movieRepository.save(m);
      });
    });
  }

  public MovieStatsResponse getMovieStats(Long id) {
    MovieStatsResponse stats = new MovieStatsResponse();
    stats.setSessionCount(sessionRepository.countByMovieId(id));
    stats.setOrderCount(Math.toIntExact(orderRepository.countByMovieId(id)));
    stats.setCommentCount(commentRepository.countByMovieId(id));
    stats.setFavoriteCount(Math.toIntExact(favoriteRepository.countByMovieId(id)));
    return stats;
  }

  // 保留/转发 DTO 风格的方法：使用父类已有的实现（如果父类有对应方法）
  // 注意：父类 MovieService 中已经定义并实现了 DTO 风格的方法，因此不必重复实现
}
