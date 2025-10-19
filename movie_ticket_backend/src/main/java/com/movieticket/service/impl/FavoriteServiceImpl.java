package com.movieticket.service.impl;

import com.movieticket.entity.Favorite;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import com.movieticket.repository.FavoriteRepository;
import com.movieticket.service.FavoriteService;
import com.movieticket.service.UserService;
import com.movieticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final MovieService movieService;

    @Override
    public Favorite addFavorite(Favorite favorite) {
        // 验证用户和电影存在
        User user = userService.getUserById(favorite.getUser().getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Movie movie = movieService.getMovieById(favorite.getMovie().getId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));

        // 检查是否已经收藏
        if (favoriteRepository.existsByUserAndMovie(user, movie)) {
            throw new RuntimeException("您已经收藏过这部电影");
        }

        favorite.setUser(user);
        favorite.setMovie(movie);

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("收藏记录不存在"));
        favoriteRepository.delete(favorite);
    }

    @Override
    public void removeFavoriteByUserAndMovie(Long userId, Long movieId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));

        Optional<Favorite> favorite = favoriteRepository.findByUserAndMovie(user, movie);
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Favorite> getFavoriteById(Long id) {
        return favoriteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return favoriteRepository.findByUserOrderByCreateTimeDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Favorite> getFavoritesByUser(Long userId, Pageable pageable) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return favoriteRepository.findByUserOrderByCreateTimeDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMovieFavoritedByUser(Long userId, Long movieId) {//判断该用户是否已收藏该电影
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        return favoriteRepository.existsByUserAndMovie(user, movie);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFavoriteCountByMovie(Long movieId) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        return favoriteRepository.countByMovie(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFavoriteCountByUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return favoriteRepository.countByUser(user);
    }
}