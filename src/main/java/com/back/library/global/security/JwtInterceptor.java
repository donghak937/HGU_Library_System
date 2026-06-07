package com.back.library.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil = JwtUtil.getInstance();

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.extractToken(authHeader);

        if (token == null || !jwtUtil.validateToken(token)) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required.");
            return false;
        }

        String requestURI = request.getRequestURI();
        String role = jwtUtil.getRole(token);

        if ("ADMIN".equals(role) && !requestURI.startsWith("/student/")) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                    "ADMIN can access student management only.");
            return false;
        }

        if (requestURI.startsWith("/student/") && !"ADMIN".equals(role)) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Only ADMIN can access student management.");
            return false;
        }

        if (requestURI.startsWith("/book/admin") && !"LIBRARIAN".equals(role)) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Only LIBRARIAN can access book management.");
            return false;
        }

        return true;
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }
}
