package com.movieticket.dto.response.session;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SessionDetailResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePoster;
    private Integer movieDuration;
    private Long hallId;
    private String hallName;
    private Integer hallCapacity;
    private Map<String, Object> seatLayout;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private Integer availableSeats;
    private Integer bookedSeats;
    private Boolean status;
    private List<String> bookedSeatNumbers;
    private List<String> lockedSeatNumbers;
}