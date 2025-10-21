package com.movieticket.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull(message = "场次不能为空")
    private Long sessionId;

    @NotNull(message = "座位不能为空")
    private List<String> seatNumbers;
}