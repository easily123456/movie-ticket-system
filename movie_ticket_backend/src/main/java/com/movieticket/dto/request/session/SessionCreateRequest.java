package com.movieticket.dto.request.session;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SessionCreateRequest {
    @NotNull(message = "电影不能为空")
    private Long movieId;

    @NotNull(message = "放映厅不能为空")
    private Long hallId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;
}