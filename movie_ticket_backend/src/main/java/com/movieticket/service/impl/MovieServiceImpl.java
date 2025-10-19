package com.movieticket.service.impl;

import com.movieticket.entity.Movie;
import com.movieticket.entity.Genre;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.CommentRepository;
import com.movieticket.service.GenreService;
import com.movieticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final GenreService genreService;

    @Override
    public Movie createMovie(Movie movie) {
        // 验证类型存在
        Genre genre = genreService.getGenreById(movie.getGenre().getId())
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));
        movie.setGenre(genre);
        movie.setStatus(true);
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Movie movie) {
        Movie existingMovie = movieRepository.findById(movie.getId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));

        // 验证类型存在
        Genre genre = genreService.getGenreById(movie.getGenre().getId())
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setOriginalTitle(movie.getOriginalTitle());
        existingMovie.setGenre(genre);
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setDirector(movie.getDirector());
        existingMovie.setActors(movie.getActors());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setCountry(movie.getCountry());
        existingMovie.setLanguage(movie.getLanguage());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setPosterUrl(movie.getPosterUrl());
        existingMovie.setTrailerUrl(movie.getTrailerUrl());
        existingMovie.setPrice(movie.getPrice());

        return movieRepository.save(existingMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movieRepository.delete(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> getActiveMovies(Pageable pageable) {
        return movieRepository.findByStatusTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByGenre(Long genreId, Pageable pageable) {
        Genre genre = genreService.getGenreById(genreId)
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));
        return movieRepository.findByGenreAndStatusTrue(genre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getHotMovies() {
        return movieRepository.findByIsHotTrueAndStatusTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getUpcomingMovies() {
        return movieRepository.findByReleaseDateAfterAndStatusTrue(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> searchMovies(String keyword, Pageable pageable) {
        return movieRepository.searchMovies(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> searchMoviesByGenre(String keyword, Long genreId, Pageable pageable) {
        return movieRepository.searchMoviesByGenre(keyword, genreId, pageable);
    }

    @Override
    public Movie changeMovieStatus(Long id, Boolean status) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setStatus(status);
        return movieRepository.save(movie);
    }

    @Override
    public Movie toggleHotMovie(Long id, Boolean isHot) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setIsHot(isHot);
        return movieRepository.save(movie);
    }

    @Override
    public void updateMovieRating(Long movieId) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));

        // 获取指定电影的平均评分
        Double averageRating = commentRepository.getAverageRatingByMovie(movieId);
        // 获取指定电影的评论总数
        long voteCount = commentRepository.countByMovie(movieId);



        if (averageRating != null) {
            movie.setRating(BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP));
            // 如果平均评分不为空，则设置电影评分为平均评分并保留一位小数，采用四舍五入模式
        } else {
            movie.setRating(BigDecimal.ZERO);// 否则将电影评分设置为零
        }


        movie.setVoteCount((int) voteCount);

        movieRepository.save(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalMovieCount() {
        return movieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveMovieCount() {
        return movieRepository.countActiveMovies();
    }
}