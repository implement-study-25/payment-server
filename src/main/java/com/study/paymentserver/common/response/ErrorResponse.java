package com.study.paymentserver.common.response;

import com.study.paymentserver.common.exception.ApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class ErrorResponse{
    private int code;
    private String message;
    private Object data;

    private ErrorResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static ResponseEntity<ErrorResponse> error(int code, String message) {
        ErrorResponse errorResponse = new ErrorResponse(code, message, null);
        return ResponseEntity.status(code).body(errorResponse);
    }

    public static ResponseEntity<ErrorResponse> error(int code, String message, Object data) {
        ErrorResponse errorResponse = new ErrorResponse(code, message, data);
        return ResponseEntity.status(code).body(errorResponse);
    }

    public static ResponseEntity<ErrorResponse> error(ApiException apiException) {
        ErrorResponse errorResponse = new ErrorResponse(apiException.getCode(), apiException.getMessage(), apiException.getData());
        return ResponseEntity.status(apiException.getCode()).body(errorResponse);
    }

}
