package com.back.library.domain.user.factory;

import com.back.library.domain.user.entity.Account;

public class AccountFactory {

    public static Account createCitizenAccount(
            String accountId,
            String username,
            String passwordHash
    ) {

        return new Account(

                accountId,

                username,

                passwordHash,

                null,

                "ACTIVE",

                "CITIZEN"
        );
    }

    public static Account createStudentAccount(
            String accountId,
            String username,
            String passwordHash
    ) {

        return new Account(

                accountId,

                username,

                passwordHash,

                null,

                "ACTIVE",

                "STUDENT"
        );
    }

    public static Account createAdminAccount(
            String accountId,
            String username,
            String passwordHash
    ) {

        return new Account(

                accountId,

                username,

                passwordHash,

                null,

                "ACTIVE",

                "ADMIN"
        );
    }

    public static Account createLibrarianAccount(
            String accountId,
            String username,
            String passwordHash
    ) {

        return new Account(

                accountId,

                username,

                passwordHash,

                null,

                "ACTIVE",

                "LIBRARIAN"
        );
    }
}