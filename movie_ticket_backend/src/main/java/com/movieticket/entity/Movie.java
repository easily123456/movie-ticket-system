package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Data
@EqualsAndHashCode(callSuper = true)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "电影标题不能为空")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "original_title", length = 200)
    private String originalTitle;

    @NotNull(message = "电影类型不能为空") //校验 genre 字段不能为 null
    @ManyToOne(fetch = FetchType.LAZY) //定义 多对一 的关联关系（多部电影可以属于同一种类型），FetchType.LAZY 表示 延迟加载，只有在真正访问 genre 字段时才会从数据库查询关联的 Genre 数据
    @JoinColumn(name = "genre_id", nullable = false)//定义 genre_id 字段，用于关联 Genre 表的 id 字段，不能为 null
    private Genre genre;

    private Integer duration; // 时长(分钟)

    @Column(length = 100)
    private String director;

    @Column(columnDefinition = "TEXT")
    private String actors;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(length = 50)
    private String country;

    @Column(length = 50)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Column(name = "trailer_url", length = 255)
    private String trailerUrl;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;//BigDecimal.ZERO是一个以BigDecimal类型表示的0.0值

    @Column(name = "vote_count")
    private Integer voteCount = 0;

    @NotNull(message = "基础价格不能为空")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_hot")
    private Boolean isHot = false;

    @Column(nullable = false)
    private Boolean status = true;
}