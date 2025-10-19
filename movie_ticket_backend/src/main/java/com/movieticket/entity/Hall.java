package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "halls")
@Data
@EqualsAndHashCode(callSuper = true)
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "放映厅名称不能为空")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "容量不能为空")
    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "seat_layout", columnDefinition = "TEXT")
    private String seatLayout; // JSON格式存储座位布局

    @Column(nullable = false)
    private Boolean status = true;
}