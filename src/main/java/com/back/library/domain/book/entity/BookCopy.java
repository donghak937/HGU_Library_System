package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 도서 사본 엔티티
 */
@Entity
@Table(name = "bookCopy") // DB 테이블 Name == bookCopy
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동 생성합니다. (JPA 작동을 위해 필수)
@Getter // 모든 필드의 값을 가져오는 getter
@Setter // 모든 필드에 값을 넣는 setter 캡슐화
public class BookCopy {
    
    @Id // 이 값이 테이블의 고유 식별자(PK)임을 뜻합니다.
    private String copyId; // 도서 사본 고유 식별 번호
    
    private String barcode; // 바코드 번호
    private String status; // 도서 상태
    private String location; // 서가 위치
}
