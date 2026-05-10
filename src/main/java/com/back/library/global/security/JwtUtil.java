package com.back.library.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // Singleton 인스턴스
    private static JwtUtil instance;

    // 외부 생성 방지
    private JwtUtil() {
    }

    // Singleton 객체 반환
    public static JwtUtil getInstance() {

        if (instance == null) {
            instance = new JwtUtil();
        }

        return instance;
    }

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "hgu-library-system-jwt-secret-key-1234567890".getBytes()
    );

    private final long accessExpirationTime = 1000 * 60 * 30; // 30분
    private final long refreshExpirationTime = 1000L * 60 * 60 * 24 * 7; // 7일

    // Access Token 생성
    public String createAccessToken(String accountId, String username, String status) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .subject(accountId)
                .claim("username", username)
                .claim("status", status)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String accountId) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .subject(accountId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // accountId 추출
    public String getAccountId(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // username 추출
    public String getUsername(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("username", String.class);
    }

    // status 추출
    public String getStatus(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("status", String.class);
    }

    // Authorization 헤더에서 Bearer 제거
    public String extractToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.replace("Bearer ", "");
    }
}