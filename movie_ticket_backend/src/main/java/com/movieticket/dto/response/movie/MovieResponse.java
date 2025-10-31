package com.movieticket.dto.response.movie;

import com.movieticket.entity.Movie;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovieResponse {
    private Long id;
    private String title;
    private String originalTitle;
    private String genreName;
    private Long genreId;
    private Integer duration;
    private String director;
    private String actors;
    private LocalDate releaseDate;
    private String country;
    private String language;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer voteCount;
    private Boolean isHot;
    private Boolean status;
    private LocalDate createTime;
    private LocalDate updateTime;
    
    // 前端显示字段
    private String formattedDuration;
    private String formattedRating;
    private Boolean isNew;

    public MovieResponse() {
    }

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.originalTitle = movie.getOriginalTitle();
        this.duration = movie.getDuration();
        this.director = movie.getDirector();
        this.actors = movie.getActors();
        this.releaseDate = movie.getReleaseDate();
        this.country = movie.getCountry();
        this.language = movie.getLanguage();
        this.description = movie.getDescription();
        this.posterUrl = movie.getPosterUrl();
        this.trailerUrl = movie.getTrailerUrl();
        this.price = movie.getPrice();
        this.rating = movie.getRating();
        this.voteCount = movie.getVoteCount();
        this.isHot = movie.getIsHot();
        this.status = movie.getStatus();
        if (movie.getGenre() != null) {
            this.genreName = movie.getGenre().getName();
        }
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}