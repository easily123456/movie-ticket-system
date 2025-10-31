package com.movieticket.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class OrderNoGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成订单号
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(formatter);
        int seq = sequence.updateAndGet(i -> i >= 9999 ? 0 : i + 1);
        return "ORD" + timestamp + String.format("%04d", seq);
    }

    /**
     * 生成退款单号
     */
    public static String generateRefundNo() {
        String timestamp = LocalDateTime.now().format(formatter);
        int seq = sequence.updateAndGet(i -> i >= 9999 ? 0 : i + 1);
        return "REF" + timestamp + String.format("%04d", seq);
    }
}