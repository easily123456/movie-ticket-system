package com.movieticket.dto.response.movie;

import com.movieticket.dto.response.comment.CommentResponse;
import com.movieticket.dto.response.session.SessionResponse;
import lombok.Data;
import java.util.List;

@Data
public class MovieDetailResponse {
    private MovieResponse movie;
    private List<SessionResponse> sessions;
    private List<CommentResponse> comments;
    private Boolean isFavorited;
    private Long favoriteCount;
}