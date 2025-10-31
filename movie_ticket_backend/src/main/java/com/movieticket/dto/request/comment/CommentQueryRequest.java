package com.movieticket.dto.request.comment;

import lombok.Data;

@Data
public class CommentQueryRequest {
    private Long movieId;
    private Long userId;
    private Boolean status = true;
    private String sortBy = "createTime";
    private String sortOrder = "desc";
    private Integer page = 0;
    private Integer size = 10;
}