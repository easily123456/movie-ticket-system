package com.movieticket.dto.response.comment;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class CommentStatsResponse {
    private Long totalComments;
    private BigDecimal averageRating;
    private Map<Integer, Long> ratingDistribution; // 评分分布
    private Long todayComments;
}