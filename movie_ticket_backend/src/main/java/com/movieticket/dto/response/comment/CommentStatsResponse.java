package com.movieticket.dto.response.comment;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CommentStatsResponse {
    private Long movieId;
    private Long totalComments;
    private BigDecimal averageRating;
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;
}