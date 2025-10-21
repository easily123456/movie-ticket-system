package com.movieticket.dto.response.movie;

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
    private BigDecimal rating;
    private Integer voteCount;
    private BigDecimal price;
    private Boolean isHot;
    private Boolean status;
}