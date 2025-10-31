package com.movieticket.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivityResponse {
    private String type; // 活动类型: ORDER, COMMENT, FAVORITE
    private String description; // 活动描述
    private LocalDateTime createTime; // 活动时间
    private Object details; // 活动详情 (根据不同类型而不同)
}