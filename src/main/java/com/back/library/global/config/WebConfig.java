package com.back.library.global.config;

import com.back.library.global.security.JwtInterceptor;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                //경로에 적힌 주소들은 인터셉터에 잡혀 토큰 유효성 검사를 함
                .addPathPatterns(
                        "/book/**",
                        "/loan/**"
                )
                //여기 경로에 적힌  주소들은 인터셉터에 잡히지 않음
                .excludePathPatterns(
                        "/account/login",
                        "/account/loginUI",
                        "/account/refresh",

                        "/book/SearchBookUI",

                        "/book/searchBooks",
                        "/book/searchBooksByCategory",
                        "/book/details",
                        "/book/copies",
                        "/loan/ReturnBookUI",
                        "/js/**",
                        "/css/**",
                        "/images/**",

                        "/h2-console/**"
                );
    }
}