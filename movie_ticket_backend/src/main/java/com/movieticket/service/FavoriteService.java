package com.movieticket.service;

import com.movieticket.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FavoriteService {
    Favorite addFavorite(Favorite favorite);
    void removeFavorite(Long id);
    void removeFavoriteByUserAndMovie(Long userId, Long movieId);
    Optional<Favorite> getFavoriteById(Long id);
    List<Favorite> getFavoritesByUser(Long userId);
    Page<Favorite> getFavoritesByUser(Long userId, Pageable pageable);

    // 获取用户收藏的电影ID列表
    List<Long> getFavoriteMovieIdsByUser(Long userId);

    boolean isMovieFavoritedByUser(Long userId, Long movieId);
    long getFavoriteCountByMovie(Long movieId);
    long getFavoriteCountByUser(Long userId);

        // 获取热门收藏电影（被收藏次数最多的）
    List<Object[]> getHotFavoriteMovies(int limit);
}