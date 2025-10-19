package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "genres")
@Data
@EqualsAndHashCode(callSuper = true)
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "类型名称不能为空")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}