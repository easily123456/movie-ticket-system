package com.movieticket.service;

import com.movieticket.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface GenreService {
    Genre createGenre(Genre genre);
    Genre updateGenre(Genre genre);
    void deleteGenre(Long id);
    Optional<Genre> getGenreById(Long id);
    List<Genre> getAllActiveGenres();
    Page<Genre> getAllGenres(Pageable pageable);
    Page<Genre> searchGenres(String keyword, Pageable pageable);
    Genre changeGenreStatus(Long id, Boolean status);
    boolean existsByName(String name);
}