package com.movieticket.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass // 映射父类
@EntityListeners(AuditingEntityListener.class)
//为实体类绑定 审计监听器，在实体生命周期事件（如保存、更新）时自动触发审计逻辑
public abstract class BaseEntity {

    @CreatedDate//在实体首次持久化（persist 或 save）时自动设置为当前时间
    @Column(name = "create_time", updatable = false)//只能由监听器修改
    private LocalDateTime createTime;

    @LastModifiedDate//在实体更新时自动设置为当前时间
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}