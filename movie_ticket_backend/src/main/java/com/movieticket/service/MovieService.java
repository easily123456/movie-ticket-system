package com.movieticket.service;

import com.movieticket.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    Movie createMovie(Movie movie);
    Movie updateMovie(Movie movie);
    void deleteMovie(Long id);
    Optional<Movie> getMovieById(Long id);
    Page<Movie> getAllMovies(Pageable pageable);
    Page<Movie> getActiveMovies(Pageable pageable);
    Page<Movie> getMoviesByGenre(Long genreId, Pageable pageable);
    List<Movie> getHotMovies();
    List<Movie> getUpcomingMovies();
    Page<Movie> searchMovies(String keyword, Pageable pageable);
    Page<Movie> searchMoviesByGenre(String keyword, Long genreId, Pageable pageable);
    Movie changeMovieStatus(Long id, Boolean status);
    Movie toggleHotMovie(Long id, Boolean isHot);
    void updateMovieRating(Long movieId);
    long getTotalMovieCount();
    long getActiveMovieCount();
}