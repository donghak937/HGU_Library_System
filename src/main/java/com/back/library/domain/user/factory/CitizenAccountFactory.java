package com.back.library.domain.user.factory;

import com.back.library.domain.user.entity.Account;

public class CitizenAccountFactory
                extends AccountFactory {

        @Override
        public Account createAccount(
                        String accountId,
                        String username,
                        String passwordHash) {

                return new Account(

                                accountId,
                                username,
                                passwordHash,
                                null,
                                "ACTIVE",
                                "CITIZEN");
        }
}