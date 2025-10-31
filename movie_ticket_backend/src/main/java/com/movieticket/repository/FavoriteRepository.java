package com.movieticket.repository;

import com.movieticket.entity.Favorite;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndMovie(User user, Movie movie);

    List<Favorite> findByUserOrderByCreateTimeDesc(User user);

    Page<Favorite> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    boolean existsByUserAndMovie(User user, Movie movie);

    long countByMovie(Movie movie);

    long countByUser(User user);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.movie.id = :movieId")
    long countByMovieId(@Param("movieId") Long movieId);






    // 根据用户和电影查找收藏
    Optional<Favorite> findByUserIdAndMovieId(Long userId, Long movieId);

    // 根据用户ID查找收藏（分页）
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    // 获取用户收藏的电影ID列表
    @Query("SELECT f.movie.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findMovieIdsByUserId(@Param("userId") Long userId);

    // 统计用户收藏数
    Long countByUserId(Long userId);

    // 获取热门收藏电影
    @Query("SELECT f.movie.id, f.movie.title, COUNT(f) as favoriteCount " +
            "FROM Favorite f " +
            "GROUP BY f.movie.id, f.movie.title " +
            "ORDER BY favoriteCount DESC")
    List<Object[]> findHotFavoriteMovies(Pageable pageable);
}