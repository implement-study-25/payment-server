package com.study.paymentserver.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class ApiResponse <T>{
    private int code;
    private T data;

    private ApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data){
        ApiResponse<T> response = new ApiResponse<>(200, data);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(int code, T data){
        ApiResponse<T> response = new ApiResponse<>(code, data);
        return ResponseEntity.status(code).body(response);
    }
}
