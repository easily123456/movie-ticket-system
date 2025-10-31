package com.movieticket.exception;

import lombok.Getter;

@Getter
//自动生成 getter 方法
public class BusinessException extends RuntimeException{

    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }


    public BusinessException(String code , String message ) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        //Throwable 是 Java 语言中所有错误或异常的超类
        super(message, cause); //利用父类构造函数，传递错误信息，构造错误对象
        this.code = "BUSINESS_ERROR";
    }
}
