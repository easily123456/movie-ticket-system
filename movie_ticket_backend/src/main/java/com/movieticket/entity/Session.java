package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "电影不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @NotNull(message = "放映厅不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")//应用层校验
    @Column(name = "end_time", nullable = false)//数据库层约束
    private LocalDateTime endTime;

    @NotNull(message = "价格不能为空")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "可用座位数不能为空")
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "booked_seats")
    private Integer bookedSeats = 0;

    @Column(nullable = false)
    private Boolean status = true;
}