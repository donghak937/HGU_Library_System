//jwt 생성 유틸리티 클래스

package com.back.library.global.security;

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

    private final long expirationTime = 1000 * 60 * 60; // 1시간

    public String createToken(String accountId, String username, String status) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(accountId)
                .claim("username", username)
                .claim("status", status)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}