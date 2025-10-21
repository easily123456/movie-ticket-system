package com.movieticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "订单编号不能为空")
    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;

    @NotNull(message = "用户不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "场次不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @NotBlank(message = "座位不能为空")
    @Column(name = "seat_numbers", nullable = false, length = 500)
    private String seatNumbers; // JSON数组存储座位号
    //由于订单的座位信息存储在数据库中，因此无法直接在实体类中定义一个 List<String> 类型的属性来存储座位号，而需要使用一个字符串来存储
    //JSON方便存储和转换

    @NotNull(message = "座位数量不能为空")
    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @NotNull(message = "总价不能为空")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)//控制枚举值在数据库中的存储形式为名称
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    public enum OrderStatus {
        PENDING, PAID, CANCELLED, REFUNDED
    }
}