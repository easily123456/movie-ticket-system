package com.movieticket.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CommentCreateRequest {
    @NotNull(message = "电影不能为空")
    private Long movieId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    @NotNull(message = "评分不能为空")
    private BigDecimal rating;
}