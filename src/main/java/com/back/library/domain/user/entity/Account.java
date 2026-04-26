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

    private boolean ssoEnabled;

    private String status;

    public Account(String accountId, String username, String passwordHash, boolean ssoEnabled, String status) {
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.ssoEnabled = ssoEnabled;
        this.status = status;
    }
}