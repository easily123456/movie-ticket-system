package com.movieticket.repository;

import com.movieticket.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);

    boolean existsByName(String name);

    List<Genre> findByStatusTrueOrderBySortOrder();

    Page<Genre> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT g FROM Genre g WHERE g.name LIKE %:keyword%")
    Page<Genre> searchGenres(String keyword, Pageable pageable);

    long countByStatus(Boolean status);

    // 管理端查询方法
    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);


    @Query("SELECT COUNT(m) FROM Movie m WHERE m.genre.id = :genreId")
    long countMoviesByGenreId(@Param("genreId") Long genreId);


}