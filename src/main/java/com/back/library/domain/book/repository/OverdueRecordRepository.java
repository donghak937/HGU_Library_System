package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.OverdueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 연체 기록 Repository.
 */
@Repository
public interface OverdueRecordRepository extends JpaRepository<OverdueRecord, String> {

    // 특정 대출의 연체 기록 조회 (중복 생성 방지용)
    Optional<OverdueRecord> findByLoanId(String loanId);

    // 특정 회원의 연체 기록 전체 조회 (viewOverdueList)
    List<OverdueRecord> findByUserId(String userId);
}
