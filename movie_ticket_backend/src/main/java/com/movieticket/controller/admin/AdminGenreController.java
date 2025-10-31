package com.movieticket.controller.admin;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Genre;
import com.movieticket.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Genre>>> getGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Genre> genres;
        if (keyword != null && !keyword.trim().isEmpty()) {
            genres = genreService.searchGenres(keyword, pageable);
        } else if (status != null) {
            genres = genreService.getGenresByStatus(status, pageable);
        } else {
            genres = genreService.getAllGenres(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(genres));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Genre>> createGenre(@RequestBody Genre genre) {
        try {
            Genre createdGenre = genreService.createGenre(genre);
            return ResponseEntity.ok(ApiResponse.success("类型创建成功", createdGenre));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
        try {
            genre.setId(id);
            Genre updatedGenre = genreService.updateGenre(genre);
            return ResponseEntity.ok(ApiResponse.success("类型更新成功", updatedGenre));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Genre>> changeGenreStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {
        try {
            Genre genre = genreService.changeGenreStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", genre));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
            return ResponseEntity.ok(ApiResponse.success("类型删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteGenres(@RequestBody List<Long> genreIds) {
        try {
            genreService.batchDeleteGenres(genreIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/disable")
    public ResponseEntity<ApiResponse<Void>> batchDisableGenres(@RequestBody List<Long> genreIds) {
        try {
            genreService.batchChangeGenreStatus(genreIds, false);
            return ResponseEntity.ok(ApiResponse.success("批量禁用成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/batch/enable")
    public ResponseEntity<ApiResponse<Void>> batchEnableGenres(@RequestBody List<Long> genreIds) {
        try {
            genreService.batchChangeGenreStatus(genreIds, true);
            return ResponseEntity.ok(ApiResponse.success("批量启用成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> getGenre(@PathVariable Long id) {
        Optional<Genre> genreOpt = genreService.getGenreById(id);
        if (genreOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(genreOpt.get()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("类型不存在"));
        }
    }
}