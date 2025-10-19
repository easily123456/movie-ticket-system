package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "system_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "配置键不能为空")
    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(length = 200)
    private String description;
}