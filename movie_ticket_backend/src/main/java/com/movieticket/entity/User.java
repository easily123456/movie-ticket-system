package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
//自动生成 equals() 和 hashCode() 方法
//equals()比较对象的相等性（基于字段值）
//hashCode()计算对象的哈希值（基于字段值）
//callSuper = true 表示在生成方法时包含父类的字段（即继承的属性也会参与比较和哈希计算）
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")//确保字符串字段不为 null、不为空字符串（""）、且至少包含一个非空白字符（即不能全是空格）
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Column(nullable = false, length = 255)
    private String password;

    @Email(message = "邮箱格式不正确")//校验字符串是否符合标准邮箱格式
    @NotBlank(message = "邮箱不能为空")
    @Column(unique = true, nullable = false,length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String avatar;

    @Enumerated(EnumType.STRING)//控制枚举值在数据库中的存储形式为名称
    @Column(nullable = false,length = 10)
    private Role role = Role.USER;//Role为自定义的枚举类，这里默认为USER

    @Column(nullable = false)
    private Boolean status=true;

    private LocalDateTime lastLoginTime;

    public enum Role{
        ADMIN,USER
    }
}
