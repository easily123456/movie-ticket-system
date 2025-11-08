package com.movieticket.service.impl;

import com.movieticket.dto.request.movie.MovieQueryRequest;
import com.movieticket.dto.response.admin.MovieStatsResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.entity.Movie;
import com.movieticket.repository.*;
import com.movieticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;

    // 获取电影列表（带筛选和分页）
    @Transactional(readOnly = true)
    @Override
    public Page<MovieResponse> getMovies(MovieQueryRequest request) {
        Specification<Movie> spec = buildSpecification(request);
        Pageable pageable = buildPageable(request);

        Page<Movie> movies = movieRepository.findAll(spec, pageable);
        return movies.map(this::convertToResponse);
    }

    @Override
    public Specification<Movie> buildSpecification(MovieQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 状态筛选（默认只查询上架电影）。
            // 如果前端请求了 releaseDateStart 或 upcoming=true，则不默认强制 status=true，保持查询灵活性。
            Boolean status = request.getStatus();
            Boolean upcoming = request.getUpcoming();
            if (status == null && request.getReleaseDateStart() == null && (upcoming == null || !upcoming)) {
                status = true;
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // 关键词搜索（标题、导演、演员）
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                String keyword = "%" + request.getKeyword().trim() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), keyword);
                Predicate directorPredicate = criteriaBuilder.like(root.get("director"), keyword);
                Predicate actorsPredicate = criteriaBuilder.like(root.get("actors"), keyword);
                predicates.add(criteriaBuilder.or(titlePredicate, directorPredicate, actorsPredicate));
            }

            // 类型筛选
            if (request.getGenreId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("genre").get("id"), request.getGenreId()));
            }

            // 热门电影筛选
            if (request.getIsHot() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isHot"), request.getIsHot()));
            }

            // 上映日期范围筛选
            if (request.getReleaseDateStart() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), request.getReleaseDateStart()));
            } else if (upcoming != null && upcoming) {
                // 如果传入 upcoming=true，默认查询 releaseDate 大于或等于今天的电影（即将上映）
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), LocalDate.now()));
            }
            if (request.getReleaseDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), request.getReleaseDateEnd()));
            }

            // 评分范围筛选
            if (request.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"),
                        BigDecimal.valueOf(request.getMinRating())));
            }
            if (request.getMaxRating() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rating"),
                        BigDecimal.valueOf(request.getMaxRating())));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Pageable buildPageable(MovieQueryRequest request) {
        Sort sort;
        // 支持 Spring Data 风格的 sort=field,direction 参数（如 rating,desc）
        if (request.getSort() != null && !request.getSort().isEmpty()) {
            String[] sortParams = request.getSort().split(",");
            if (sortParams.length == 2) {
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1].trim());
                sort = Sort.by(direction, sortParams[0].trim());
            } else {
                // 仅提供字段时，默认降序
                sort = Sort.by(Sort.Direction.DESC, sortParams[0].trim());
            }
        } else {
            // 兼容旧参数：sortBy + sortOrder
            Sort.Direction direction = Sort.Direction.fromString(request.getSortOrder());
            sort = Sort.by(direction, request.getSortBy());
        }
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> getHotMovies(int limit) {
        List<Movie> movies = movieRepository.findByIsHotAndStatusOrderByRatingDesc(true, true,
                PageRequest.of(0, limit));
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> getNewMovies(int limit) { // 2022-07-01后的电影
        LocalDate time = LocalDate.of(2022, 7, 1);
        List<Movie> movies = movieRepository.findByReleaseDateAfterAndStatusAndIsHotOrderByReleaseDateDesc(time, true,
                false,
                PageRequest.of(0, limit));
        // 返回2022年7月1日之后的电影
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> getTopRatedMovies(int limit) {
        List<Movie> movies = movieRepository.findByStatusAndRatingGreaterThanEqualOrderByRatingDesc(true,
                new BigDecimal("8.0"), PageRequest.of(0, limit));
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MovieResponse> searchMovies(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.searchMovies(keyword, pageable);
        return movies.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MovieResponse> getMoviesByGenre(Long genreId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByGenreIdAndStatus(genreId, true, pageable);
        return movies.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MovieResponse> getMovieDetail(Long id) {
        return movieRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public MovieResponse convertToResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setOriginalTitle(movie.getOriginalTitle());
        response.setGenreName(movie.getGenre().getName());
        response.setGenreId(movie.getGenre().getId());
        response.setDuration(movie.getDuration());
        response.setDirector(movie.getDirector());
        response.setActors(movie.getActors());
        response.setReleaseDate(movie.getReleaseDate());
        response.setCountry(movie.getCountry());
        response.setLanguage(movie.getLanguage());
        response.setDescription(movie.getDescription());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setRating(movie.getRating());
        response.setVoteCount(movie.getVoteCount());
        response.setPrice(movie.getPrice());
        response.setIsHot(movie.getIsHot());
        response.setStatus(movie.getStatus());
        response.setCreateTime(movie.getCreateTime().toLocalDate());
        response.setUpdateTime(movie.getUpdateTime().toLocalDate());

        // 计算前端显示字段
        response.setFormattedDuration(formatDuration(movie.getDuration()));
        response.setFormattedRating(formatRating(movie.getRating()));
        response.setIsNew(isNewMovie(movie.getReleaseDate()));

        // 填充基于评论的平均分（只用于展示，不持久化到 movie 实体）
        try {
            Double avg = commentRepository.getAverageRatingByMovie(movie.getId());
            response.setCommentAverageRating(avg != null ? java.math.BigDecimal.valueOf(avg) : null);
        } catch (Exception e) {
            // 获取评论平均分为非关键路径，若失败则保持为空
            response.setCommentAverageRating(null);
        }

        return response;
    }

    @Override
    public String formatDuration(Integer duration) {
        if (duration == null)
            return "未知";
        int hours = duration / 60;
        int minutes = duration % 60;
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        } else {
            return minutes + "分钟";
        }
    }

    // 格式化评分
    @Override
    public String formatRating(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.ZERO) == 0) {
            return "暂无评分";
        }
        return rating + "分";
    }

    // 判断是否是新电影（一个月内上映）
    @Override
    public Boolean isNewMovie(LocalDate releaseDate) {
        if (releaseDate == null)
            return false;
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return !releaseDate.isBefore(oneMonthAgo);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }
    // ---------- 实体级/管理端需要的方法 (Controller/Service 依赖) ----------

    @Override
    public void updateMovieRating(Long movieId) {
        Double avg = commentRepository.getAverageRatingByMovie(movieId);
        long count = commentRepository.countByMovieId(movieId);
        com.movieticket.entity.Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null)
            return;
        if (avg == null) {
            movie.setRating(java.math.BigDecimal.ZERO);
        } else {
            movie.setRating(java.math.BigDecimal.valueOf(avg));
        }
        movie.setVoteCount((int) count);
        movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    @Override
    public long getTotalMovieCount() {
        return movieRepository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public long getActiveMovieCount() {
        return movieRepository.countActiveMovies();
    }

    @Override
    public com.movieticket.entity.Movie createMovie(com.movieticket.entity.Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public com.movieticket.entity.Movie updateMovie(com.movieticket.entity.Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public com.movieticket.entity.Movie changeMovieStatus(Long id, Boolean status) {
        com.movieticket.entity.Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setStatus(status);
        return movieRepository.save(movie);
    }

    @Override
    public com.movieticket.entity.Movie setMovieHot(Long id, Boolean isHot) {
        com.movieticket.entity.Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setIsHot(isHot);
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public void batchDeleteMovies(java.util.List<Long> movieIds) {
        movieRepository.deleteAllById(movieIds);
    }

    @Override
    public void batchChangeMovieStatus(java.util.List<Long> movieIds, boolean status) {
        movieIds.forEach(id -> movieRepository.findById(id).ifPresent(m -> {
            m.setStatus(status);
            movieRepository.save(m);
        }));
    }

    @Override
    public void batchSetMoviesHot(java.util.List<Long> movieIds, Boolean isHot) {
        movieIds.forEach(id -> movieRepository.findById(id).ifPresent(m -> {
            m.setIsHot(isHot);
            movieRepository.save(m);
        }));
    }

    @Transactional(readOnly = true)
    @Override
    public MovieStatsResponse getMovieStats(Long id) {
        MovieStatsResponse stats = new MovieStatsResponse();
        stats.setSessionCount(sessionRepository.countByMovieId(id));
        stats.setOrderCount(Math.toIntExact(orderRepository.countByMovieId(id)));
        stats.setCommentCount(commentRepository.countByMovieId(id));
        stats.setFavoriteCount(Math.toIntExact(favoriteRepository.countByMovieId(id)));
        return stats;
    }
}