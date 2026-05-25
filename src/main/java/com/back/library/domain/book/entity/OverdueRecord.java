package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 연체 기록 엔티티 (createOverdueRecord).
 * 연체가 감지된 대출에 대해 생성되며, 연체료 계산 및 정지 처리의 기반 데이터가 된다.
 */
@Entity
@Table(name = "overdue_record")
@NoArgsConstructor
@Getter
@Setter
public class OverdueRecord {

    @Id
    private String overdueId;        // 연체 기록 고유 ID

    private String  loanId;          // 연체된 대출 ID (Loan 참조)
    private String  userId;          // 연체 회원 ID
    private Date    detectedAt;      // 연체 감지 일시
    private int     overdueDays;     // 연체일수
    private boolean penaltyApplied;  // 연체료 적용 여부 (applyLatePenalty)
    private boolean suspendApplied;  // 대출 정지 적용 여부 (isSuspended 설정)
}
