package com.movieticket.exception;

import com.movieticket.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        //ResponseEntity<ApiResponse<Object>>返回错误信息
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
        //ResponseEntity.status(HttpStatus.BAD_REQUEST)设置响应状态码为400
        //body()将 ApiResponse 对象作为响应体，并自动序列化为 JSON
        //HttpStatus.BAD_REQUEST和e.getCode()的区别
        //HttpStatus.BAD_REQUEST表示 HTTP 协议层面的请求处理结果（由服务器返回给客户端的通用状态码）
        //e.getCode()表示业务逻辑层面的具体错误类型（由应用自定义，用于客户端精确处理错误）
    }


    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthException e) {
        log.warn("认证异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)//添加参数验证异常处理
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();//getField()：获取校验失败的字段名，（FieldError) error强行转换为FieldError，方便获取字段名
            String errorMessage = error.getDefaultMessage();//getDefaultMessage()：获取校验失败时的错误消息
            errors.put(fieldName, errorMessage);//将错误信息存储在 errors 中
        });
        //e.getBindingResult()获取MethodArgumentNotValidException 包含一个 BindingResult 对象，存储了所有校验失败的信息
        //getAllErrors()获取所有校验错误
        //forEach()遍历所有错误，将每个错误信息存储在 errors 中

        log.warn("参数验证异常: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", "参数验证失败", errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)//处理文件大小超出限制异常
    public ResponseEntity<ApiResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException e) {
        log.warn("文件大小超出限制: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("FILE_TOO_LARGE", "文件大小超出限制"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("SYSTEM_ERROR", "系统异常，请稍后重试"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("RUNTIME_ERROR", e.getMessage()));
    }

}


