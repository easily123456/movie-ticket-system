package com.movieticket.dto.request.movie;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MovieQueryRequest {
    private String keyword;
    private Long genreId;
    private Boolean isHot;
    private Boolean status = true;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateEnd;

    private Double minRating;
    private Double maxRating;

    private String sortBy = "createTime";
    private String sortOrder = "desc";

    private Integer page = 0;
    private Integer size = 12;
}