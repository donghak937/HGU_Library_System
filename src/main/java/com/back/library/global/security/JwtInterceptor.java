package com.back.library.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    // Singleton 객체 가져오기
    private final JwtUtil jwtUtil = JwtUtil.getInstance();

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.extractToken(authHeader);

        // 토큰 검증
        if (token == null || !jwtUtil.validateToken(token)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");

            response.getWriter().write(
                    "{\"success\": false, \"message\": \"인증이 필요합니다.\"}"
            );

            return false;
        }

        return true;
    }
}