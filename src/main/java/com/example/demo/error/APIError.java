package com.example.demo.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class APIError extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public APIError(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
