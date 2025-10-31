package com.movieticket.dto.response.admin;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RevenueDataResponse {
    private List<String> labels;
    private List<BigDecimal> data;
    private LocalDate date;
    private BigDecimal revenue;
    private Integer orderCount;
    private Integer userCount;
}