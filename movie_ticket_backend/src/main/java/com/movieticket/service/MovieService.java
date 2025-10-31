package com.movieticket.service;

import com.movieticket.dto.request.movie.MovieQueryRequest;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.dto.response.admin.MovieStatsResponse;
import com.movieticket.entity.Movie;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.CommentRepository;
import com.movieticket.repository.FavoriteRepository;
import com.movieticket.repository.OrderRepository;
import com.movieticket.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;

    // 获取电影列表（带筛选和分页）
    public Page<MovieResponse> getMovies(MovieQueryRequest request) {
        Specification<Movie> spec = buildSpecification(request);
        Pageable pageable = buildPageable(request);

        Page<Movie> movies = movieRepository.findAll(spec, pageable);
        return movies.map(this::convertToResponse);
    }

    // 构建查询条件
    private Specification<Movie> buildSpecification(MovieQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 状态筛选（默认只查询上架电影）
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
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

    // 构建分页和排序
    private Pageable buildPageable(MovieQueryRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortOrder()), request.getSortBy());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    // 获取热门电影
    public List<MovieResponse> getHotMovies(int limit) {
        List<Movie> movies = movieRepository.findByIsHotAndStatusOrderByRatingDesc(true, true,
                PageRequest.of(0, limit));
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 获取最新上映电影
    public List<MovieResponse> getNewMovies(int limit) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Movie> movies = movieRepository.findByReleaseDateAfterAndStatusOrderByReleaseDateDesc(oneMonthAgo, true,
                PageRequest.of(0, limit));
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 获取高分电影
    public List<MovieResponse> getTopRatedMovies(int limit) {
        List<Movie> movies = movieRepository.findByStatusAndRatingGreaterThanEqualOrderByRatingDesc(true,
                new BigDecimal("8.0"), PageRequest.of(0, limit));
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 搜索电影
    public Page<MovieResponse> searchMovies(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.searchMovies(keyword, pageable);
        return movies.map(this::convertToResponse);
    }

    // 根据类型获取电影
    public Page<MovieResponse> getMoviesByGenre(Long genreId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByGenreIdAndStatus(genreId, true, pageable);
        return movies.map(this::convertToResponse);
    }

    // 获取电影详情
    public Optional<MovieResponse> getMovieDetail(Long id) {
        return movieRepository.findById(id)
                .map(this::convertToResponse);
    }

    // 转换实体为响应DTO
    private MovieResponse convertToResponse(Movie movie) {
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

        return response;
    }

    // 格式化时长
    private String formatDuration(Integer duration) {
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
    private String formatRating(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.ZERO) == 0) {
            return "暂无评分";
        }
        return rating + "分";
    }

    // 判断是否是新电影（一个月内上映）
    private Boolean isNewMovie(LocalDate releaseDate) {
        if (releaseDate == null)
            return false;
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return !releaseDate.isBefore(oneMonthAgo);
    }

    // ---------- 实体级/管理端需要的方法 (Controller/Service 依赖) ----------
    public java.util.Optional<com.movieticket.entity.Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

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

    public long getTotalMovieCount() {
        return movieRepository.count();
    }

    public long getActiveMovieCount() {
        return movieRepository.countActiveMovies();
    }

    public com.movieticket.entity.Movie createMovie(com.movieticket.entity.Movie movie) {
        return movieRepository.save(movie);
    }

    public com.movieticket.entity.Movie updateMovie(com.movieticket.entity.Movie movie) {
        return movieRepository.save(movie);
    }

    public com.movieticket.entity.Movie changeMovieStatus(Long id, Boolean status) {
        com.movieticket.entity.Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setStatus(status);
        return movieRepository.save(movie);
    }

    public com.movieticket.entity.Movie setMovieHot(Long id, Boolean isHot) {
        com.movieticket.entity.Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        movie.setIsHot(isHot);
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public void batchDeleteMovies(java.util.List<Long> movieIds) {
        movieRepository.deleteAllById(movieIds);
    }

    public void batchChangeMovieStatus(java.util.List<Long> movieIds, boolean status) {
        movieIds.forEach(id -> movieRepository.findById(id).ifPresent(m -> {
            m.setStatus(status);
            movieRepository.save(m);
        }));
    }

    public void batchSetMoviesHot(java.util.List<Long> movieIds, Boolean isHot) {
        movieIds.forEach(id -> movieRepository.findById(id).ifPresent(m -> {
            m.setIsHot(isHot);
            movieRepository.save(m);
        }));
    }

    public MovieStatsResponse getMovieStats(Long id) {
        MovieStatsResponse stats = new MovieStatsResponse();
        stats.setSessionCount(sessionRepository.countByMovieId(id));
        stats.setOrderCount(Math.toIntExact(orderRepository.countByMovieId(id)));
        stats.setCommentCount(commentRepository.countByMovieId(id));
        stats.setFavoriteCount(Math.toIntExact(favoriteRepository.countByMovieId(id)));
        return stats;
    }
}