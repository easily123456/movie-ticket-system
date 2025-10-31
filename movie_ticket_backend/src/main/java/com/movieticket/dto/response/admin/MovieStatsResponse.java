package com.movieticket.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovieStatsResponse {
    private Long sessionCount;
    private Long commentCount;


    private Long movieId;
    private String movieTitle;
    private Integer viewCount;
    private Integer orderCount;
    private BigDecimal revenue;
    private Integer favoriteCount;
    private Double rating;
}