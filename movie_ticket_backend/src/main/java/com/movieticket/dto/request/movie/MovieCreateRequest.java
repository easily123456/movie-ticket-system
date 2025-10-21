package com.movieticket.dto.request.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovieCreateRequest {
    @NotBlank(message = "电影标题不能为空")
    private String title;

    private String originalTitle;

    @NotNull(message = "电影类型不能为空")
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

    @NotNull(message = "基础价格不能为空")
    private BigDecimal price;
}