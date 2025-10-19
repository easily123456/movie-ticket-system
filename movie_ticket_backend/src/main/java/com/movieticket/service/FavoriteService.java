package com.movieticket.service;

import com.movieticket.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface FavoriteService {
    Favorite addFavorite(Favorite favorite);
    void removeFavorite(Long id);
    void removeFavoriteByUserAndMovie(Long userId, Long movieId);
    Optional<Favorite> getFavoriteById(Long id);
    List<Favorite> getFavoritesByUser(Long userId);
    Page<Favorite> getFavoritesByUser(Long userId, Pageable pageable);
    boolean isMovieFavoritedByUser(Long userId, Long movieId);
    long getFavoriteCountByMovie(Long movieId);
    long getFavoriteCountByUser(Long userId);
}