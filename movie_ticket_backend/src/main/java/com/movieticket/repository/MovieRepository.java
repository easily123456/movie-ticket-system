package com.movieticket.repository;

import com.movieticket.entity.Movie;
import com.movieticket.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByStatusTrue(Pageable pageable);

    Page<Movie> findByGenreAndStatusTrue(Genre genre, Pageable pageable);

    List<Movie> findByIsHotTrueAndStatusTrue();

    List<Movie> findByReleaseDateAfterAndStatusTrue(LocalDate date);

    @Query("SELECT m FROM Movie m WHERE m.status = true AND " +
            "(m.title LIKE %:keyword% OR m.originalTitle LIKE %:keyword% OR m.director LIKE %:keyword%)")
    Page<Movie> searchMovies(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.status = true AND m.genre.id = :genreId AND " +
            "(m.title LIKE %:keyword% OR m.originalTitle LIKE %:keyword%)")
    Page<Movie> searchMoviesByGenre(@Param("keyword") String keyword, @Param("genreId") Long genreId, Pageable pageable);
    //%:keyword%是模糊匹配，因此使用占位符，

    Page<Movie> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.status = true")
    long countActiveMovies();

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.isHot = true AND m.status = true")
    long countHotMovies();
}