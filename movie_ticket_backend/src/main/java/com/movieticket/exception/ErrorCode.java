package com.movieticket.exception;

public enum ErrorCode {
    // 认证相关
    UNAUTHORIZED("AUTH_001", "未授权访问"),
    TOKEN_EXPIRED("AUTH_002", "Token已过期"),
    TOKEN_INVALID("AUTH_003", "Token无效"),
    LOGIN_FAILED("AUTH_004", "登录失败"),

    // 用户相关
    USER_NOT_FOUND("USER_001", "用户不存在"),
    USER_EXISTS("USER_002", "用户已存在"),
    PASSWORD_ERROR("USER_003", "密码错误"),

    // 电影相关
    MOVIE_NOT_FOUND("MOVIE_001", "电影不存在"),
    MOVIE_EXISTS("MOVIE_002", "电影已存在"),

    // 场次相关
    SESSION_NOT_FOUND("SESSION_001", "场次不存在"),
    SESSION_CONFLICT("SESSION_002", "场次时间冲突"),

    // 订单相关
    ORDER_NOT_FOUND("ORDER_001", "订单不存在"),
    ORDER_STATUS_ERROR("ORDER_002", "订单状态错误"),
    SEAT_NOT_AVAILABLE("ORDER_003", "座位不可用"),

    // 系统相关
    SYSTEM_ERROR("SYS_001", "系统异常"),
    VALIDATION_ERROR("SYS_002", "参数验证失败"),
    FILE_TOO_LARGE("SYS_003", "文件大小超出限制");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}