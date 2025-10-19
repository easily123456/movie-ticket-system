package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "电影不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @NotBlank(message = "评论内容不能为空")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "评分不能为空")
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Boolean status = true;
}