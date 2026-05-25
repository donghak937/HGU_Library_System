package com.back.library.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Account {

    @Id
    private String accountId;

    @Column(unique = true)
    private String username;

    private String passwordHash;

    @Column(length = 1000)
    private String refreshToken;

    private String status;

    private String role; // "STUDENT", "PROFESSOR", "LIBRARIAN", "ADMIN"

    public Account(String accountId, String username, String passwordHash, String refreshToken, String status, String role) {
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.refreshToken = refreshToken;
        this.status = status;
        this.role = role;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}