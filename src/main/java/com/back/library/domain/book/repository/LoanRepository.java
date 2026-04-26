package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {
    long countByUserIdAndStatus(String userId, String status);
    java.util.List<Loan> findByUserIdAndStatus(String userId, String status);
}
