package com.movieticket.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String code;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.code = success ? "SUCCESS" : "ERROR";
    }

    public static <T> ApiResponse<T> success(T data) {//静态工厂方法
        //上行的第一个<T>为声明泛型参数，第二个<T>为返回值类型，第三个<T>为参数类型
        return new ApiResponse<>(true, "操作成功", data);
    }
    //为什么使用静态工厂方法，因为创建对象实例需要多个参数，而静态方法只需要一个参数，并且返回一个对象实例
    //控制对象的创建逻辑，确保 code 和 success 字段始终匹配（如 success=true 时 code="SUCCESS"），避免success=true 但 code="ERROR"
    //调用 ApiResponse.success(data) 比 new ApiResponse(true, "操作成功", data) 更直观

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        response.setCode(code);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message,T errors) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        response.setCode(code);
        response.setData(errors);
        return response;
    }

}
