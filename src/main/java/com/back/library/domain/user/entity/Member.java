package com.back.library.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 도서관 회원 엔티티
 */
@Entity
@Table(name = "member") // DB 테이블 Name == member
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동 생성합니다. (JPA 작동을 위해 필수)
@Getter // 모든 필드의 값을 가져오는 getter
@Setter // 모든 필드에 값을 넣는 setter 캡슐화
public class Member {
    
    @Id // 이 값이 테이블의 고유 식별자(PK)임을 뜻합니다.
    private String userId; // 회원 고유 아이디
    
    private int maxLoanLimit; // 최대 대출 가능 권수
    private int loanPeriod; // 대출 허용 기간 (일)
    private boolean isSuspended; // 대출 정지 여부
    private java.util.Date suspensionEndDate; // 정지 종료일
}
