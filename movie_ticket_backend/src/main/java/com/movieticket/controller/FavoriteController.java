package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.response.movie.MovieResponse;
import com.movieticket.entity.Favorite;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import com.movieticket.service.FavoriteService;
import com.movieticket.service.MovieService;
import com.movieticket.service.UserService;
import com.movieticket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    private final MovieService movieService;
    private final JwtUtil jwtUtil;

    @PostMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long movieId) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Optional<User> userOpt = userService.getUserById(userId);
            Optional<Movie> movieOpt = movieService.getMovieById(movieId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }
            if (movieOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("电影不存在"));
            }

            User user = userOpt.get();
            Movie movie = movieOpt.get();

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMovie(movie);

            favoriteService.addFavorite(favorite);
            return ResponseEntity.ok(ApiResponse.success("收藏成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("收藏失败"));
        }
    }

    @DeleteMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long movieId) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            favoriteService.removeFavoriteByUserAndMovie(userId, movieId);
            return ResponseEntity.ok(ApiResponse.success("取消收藏成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消收藏失败"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getFavorites(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            Pageable pageable = PageRequest.of(page, size);
            Page<Favorite> favorites = favoriteService.getFavoritesByUser(userId, pageable);
            Page<MovieResponse> response = favorites.map(favorite -> convertToMovieResponse(favorite.getMovie()));
            //Page<Favorite>转换为Page<MovieResponse>
            //1. favorite -> convertToMovieResponse(favorite.getMovie())接收一个Favorite对象
            //2. 调用favorite.getMovie()获取对应的Movie对象
            //3. convertToMovieResponse(favorite.getMovie())将Movie对象转换为MovieResponse对象


            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取收藏列表失败"));
        }
    }

    @GetMapping("/check/movie/{movieId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFavorite(//检查用户是否收藏了某个电影
            @RequestHeader("Authorization") String token,
            @PathVariable Long movieId) {
        try {
            String authToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(authToken);

            boolean isFavorited = favoriteService.isMovieFavoritedByUser(userId, movieId);
            return ResponseEntity.ok(ApiResponse.success(isFavorited));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("检查收藏状态失败"));
        }
    }

    private MovieResponse convertToMovieResponse(Movie movie) {
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
        return response;
    }
}