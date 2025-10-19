package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Data
@EqualsAndHashCode(callSuper = true)
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "资讯标题不能为空")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "资讯内容不能为空")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(length = 50)
    private String author;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_top")
    private Boolean isTop = false;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;
}