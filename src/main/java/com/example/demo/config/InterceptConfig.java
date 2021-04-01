package com.example.demo.config;

import com.example.demo.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class InterceptConfig implements WebMvcConfigurer {
    private final AuthenticationHandler authenticationHandler;

    @Autowired
    public InterceptConfig(
            AuthenticationHandler authenticationHandler
    ) {
        super();
        this.authenticationHandler = authenticationHandler;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationHandler);
    }
}
