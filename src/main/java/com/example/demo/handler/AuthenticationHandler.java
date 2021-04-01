package com.example.demo.handler;

import com.example.demo.annotation.RequestUser;
import com.example.demo.error.APIError;
import com.example.demo.model.entity.User;
import com.example.demo.service.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Configuration
public class AuthenticationHandler implements HandlerMethodArgumentResolver {
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestUser.class);
    }

    @Autowired
    AuthenticationHandler(
            JwtTokenProvider jwtTokenProvider
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Optional<User> resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        RequestUser userAnnotation = parameter.getParameterAnnotation(RequestUser.class);
        String token = webRequest.getHeader("Authorization");

        Optional<User> user = (token == null) ? Optional.empty() : jwtTokenProvider.parseToken(token);

        if (userAnnotation.required() && user.isEmpty()) {
            // JWT Token 값이 잘못되었거나 유저 정보가 존재하지 않을 때는
            // 인증이 되지 않았음을 의미하므로, API 가 실행되기 전에 에러를
            // 호출합니다. 유저 정보가 없을 때 에러를 호출할 필요가 없는 경우에는
            // `@Authenticate(required=false)` 로 annotation 을 넘기면 됩니다.
            throw new APIError("AUTHENTICATION_ERROR", "Not authorized", HttpStatus.UNAUTHORIZED);
        }

        return user;
    }
}
