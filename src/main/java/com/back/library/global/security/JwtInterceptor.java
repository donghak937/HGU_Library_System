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

        // 도서 관리자 기능 권한 검증 (사서 또는 어드민)
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/book/admin") && !requestURI.equals("/book/admin/BookManagementUI")) {
            String role = jwtUtil.getRole(token);
            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(
                        "{\"success\": false, \"message\": \"권한이 없습니다. 사서만 접근 가능합니다.\"}"
                );
                return false;
            }
        }

        if (requestURI.startsWith("/student/")) {
            String role = jwtUtil.getRole(token);
            if (!"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(
                        "{\"success\": false, \"message\": \"학생 관리는 관리자만 접근할 수 있습니다.\"}"
                );
                return false;
            }
        }

        return true;
    }
}
