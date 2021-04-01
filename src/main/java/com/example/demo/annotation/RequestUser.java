package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Request Header 의 Authorization 에 담긴 JWT Token 으로 유저 정보를 주입합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestUser {
    /**
     * True 일 경우 JWT Token 이 없거나 유효하지 못한 값이면 Error 가 발생하고,
     * API 가 실행되지 않습니다.
     * @return API 가 실행될 때 유저 인증 필수 여부.
     */
    boolean required() default true;
}
