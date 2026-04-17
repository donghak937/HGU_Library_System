package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 도서 대출 기록을 저장하는 엔티티.
 */
@Entity
@Table(name = "loan") // DB 테이블 Name == loan
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동 생성합니다. (JPA 작동을 위해 필수)
@Getter // 모든 필드의 값을 가져오는 getter
@Setter // 모든 필드에 값을 넣는 setter 캡슐화
public class Loan {

    @Id // 이 값이 테이블의 고유 식별자(PK)임을 뜻합니다.
    private String loanId; // 대출 고유 식별 번호

    private java.util.Date loanDate; // 대출이 실행된 날짜 및 시간
    private java.util.Date dueDate; // 반납해야 할 기한 날짜
    private java.util.Date returnDate; // 실제 도서를 반납한 날짜
    private String status; // 대출 상태

    // 관계형 데이터베이스의 외래키(Foreign Key) 역할을 하는 연결 ID들입니다.
    private String userId; // 누가 책을 빌렸는지 기억하는 아이디
    private String copyId; // 어떤 책을 빌렸는지 기억하는 아이디
}
