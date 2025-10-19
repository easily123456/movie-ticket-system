package com.movieticket.service.impl;

import com.movieticket.entity.Genre;
import com.movieticket.repository.GenreRepository;
import com.movieticket.service.GenreService;
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
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByName(genre.getName())) {
            throw new RuntimeException("电影类型名称已存在");
        }
        genre.setStatus(true);
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        Genre existingGenre = genreRepository.findById(genre.getId())
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));

        if (!existingGenre.getName().equals(genre.getName()) &&
                genreRepository.existsByName(genre.getName())) {
            throw new RuntimeException("电影类型名称已存在");
        }

        existingGenre.setName(genre.getName());
        existingGenre.setDescription(genre.getDescription());
        existingGenre.setSortOrder(genre.getSortOrder());

        return genreRepository.save(existingGenre);
    }

    @Override
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));

        // 检查是否有电影使用该类型
//        if (!genre.getMovies().isEmpty()) {
//            throw new RuntimeException("该类型下存在电影，无法删除");
//        }

        genreRepository.delete(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllActiveGenres() {
        return genreRepository.findByStatusTrueOrderBySortOrder();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Genre> searchGenres(String keyword, Pageable pageable) {
        return genreRepository.searchGenres(keyword, pageable);
    }

    @Override
    public Genre changeGenreStatus(Long id, Boolean status) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影类型不存在"));
        genre.setStatus(status);
        return genreRepository.save(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return genreRepository.existsByName(name);
    }
}