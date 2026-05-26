package com.back.library.domain.user.repository;

import com.back.library.domain.user.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, String> {

    Optional<Account> findByUsername(
            String username
    );

    @Query("""
        SELECT MAX(a.accountId)
        FROM Account a
    """)
    String findMaxAccountId();
}