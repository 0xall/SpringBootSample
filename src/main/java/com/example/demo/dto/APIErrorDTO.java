package com.example.demo.dto;

import lombok.Getter;

@Getter
public class APIErrorDTO {
    private String errorCode;
    private String message;

    private APIErrorDTO() {
    }

    public APIErrorDTO(String errorCode, String message) {
        this();
        this.errorCode = errorCode;
        this.message = message;
    }
}
