package com.movieticket.dto.response.comment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private Long movieId;
    private String content;
    private BigDecimal rating;
    private Integer likeCount;
    private LocalDateTime createTime;
}