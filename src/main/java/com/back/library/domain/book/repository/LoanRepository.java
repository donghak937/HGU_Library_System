package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {
    long countByUserIdAndStatus(String userId, String status);
    java.util.List<Loan> findByUserIdAndStatus(String userId, String status);
    // 자동 연체 감지 스케줄러용: 상태 "대출중" + 반납기한 초과
    @Query("SELECT l FROM Loan l WHERE l.status = '대출중' AND l.dueDate < CURRENT_TIMESTAMP")
    List<Loan> findAllOverdueLoans();
}