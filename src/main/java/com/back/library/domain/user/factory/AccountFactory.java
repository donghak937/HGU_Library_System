package com.back.library.domain.user.factory;

import com.back.library.domain.user.entity.Account;

public abstract class AccountFactory {

    // Factory Method
    public abstract Account createAccount(
            String accountId,
            String username,
            String passwordHash
    );
}