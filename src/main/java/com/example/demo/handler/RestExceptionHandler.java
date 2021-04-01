package com.example.demo.handler;

import com.example.demo.dto.APIErrorDTO;
import com.example.demo.error.APIError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(APIError.class)
    protected ResponseEntity<Object> handleAPIError(APIError apiError) {
        APIErrorDTO body = new APIErrorDTO(apiError.getErrorCode(), apiError.getMessage());
        return new ResponseEntity<>(body, apiError.getStatus());
    }
}