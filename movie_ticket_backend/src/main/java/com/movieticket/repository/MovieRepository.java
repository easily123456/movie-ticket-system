package com.movieticket.repository;

import com.movieticket.entity.Movie;
import com.movieticket.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    Page<Movie> findByStatusTrue(Pageable pageable);

    Page<Movie> findByGenreAndStatusTrue(Genre genre, Pageable pageable);

    List<Movie> findByIsHotTrueAndStatusTrue();

    List<Movie> findByReleaseDateAfterAndStatusTrue(LocalDate date);

    @Query("SELECT m FROM Movie m WHERE m.status = true AND m.genre.id = :genreId AND " +
            "(m.title LIKE %:keyword% OR m.originalTitle LIKE %:keyword%)")
    Page<Movie> searchMoviesByGenre(@Param("keyword") String keyword, @Param("genreId") Long genreId, Pageable pageable);
    //%:keyword%是模糊匹配，因此使用占位符，

    Page<Movie> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.status = true")
    long countActiveMovies();

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.isHot = true AND m.status = true")
    long countHotMovies();

    Page<Movie> findByIsHot(Boolean isHot, Pageable pageable);

    boolean existsByTitle(String title);

//    @Query("SELECT m FROM Movie m WHERE " +
//            "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(m.director) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(m.actors) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    Page<Movie> searchMovies(@Param("keyword") String keywo*/rd, Pageable pageable);




    // 热门电影
    List<Movie> findByIsHotAndStatusOrderByRatingDesc(Boolean isHot, Boolean status, Pageable pageable);

    // 最新上映电影
    List<Movie> findByReleaseDateAfterAndStatusOrderByReleaseDateDesc(LocalDate releaseDate, Boolean status, Pageable pageable);

    // 高分电影
    List<Movie> findByStatusAndRatingGreaterThanEqualOrderByRatingDesc(Boolean status, BigDecimal minRating, Pageable pageable);

    // 根据类型查询
    Page<Movie> findByGenreIdAndStatus(Long genreId, Boolean status, Pageable pageable);

    // 搜索电影（标题、导演、演员）
    @Query("SELECT m FROM Movie m WHERE " +
            "(m.title LIKE %:keyword% OR m.director LIKE %:keyword% OR m.actors LIKE %:keyword%) " +
            "AND m.status = true")
    Page<Movie> searchMovies(@Param("keyword") String keyword, Pageable pageable);

    // 获取所有电影类型（去重）
    @Query("SELECT DISTINCT m.genre.id, m.genre.name FROM Movie m WHERE m.status = true")
    List<Object[]> findDistinctGenres();

    // 获取评分统计
    @Query("SELECT AVG(m.rating), COUNT(m) FROM Movie m WHERE m.status = true AND m.rating > 0")
    Object[] getRatingStats();

    Long   countByStatus(boolean status);
}