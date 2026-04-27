// jwt 생성 유틸리티 클래스
package com.back.library.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "hgu-library-system-jwt-secret-key-1234567890".getBytes()
    );

    private final long accessExpirationTime = 1000 * 60 * 3; // 30분
    private final long refreshExpirationTime = 1000L * 60 * 60 * 24 * 7; // 7일

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

    public String getAccountId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}