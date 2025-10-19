package com.movieticket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {
    private static final AtomicInteger sequence = new AtomicInteger(1);//生成一个原子性的序列号，初始值为1
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    //定义时间格式化规则，用于生成时间戳部分

    public static String generate() {
        String timestamp = LocalDateTime.now().format(formatter);
        int seq = sequence.getAndIncrement() % 10000;
        return "ORDER" + timestamp + String.format("%04d", seq);
        //ORDER + 时间戳（yyyyMMddHHmmss） + 4位序列号
    }
}
