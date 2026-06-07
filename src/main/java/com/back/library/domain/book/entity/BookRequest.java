package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 교원/대학원생 도서 구입 요청 엔티티.
 * PROFESSOR, FACULTY_GRADUATE 역할만 요청 가능하다.
 *
 * 상태 흐름:
 *   PENDING → APPROVED (사서 승인) → ADDED (책 DB 등록 완료)
 *   PENDING → REJECTED (사서 반려)
 */
@Entity
@Table(name = "book_request")
@NoArgsConstructor
@Getter
@Setter
public class BookRequest {

    @Id
    private String requestId;       // 요청 고유 ID

    private String requesterId;     // 요청자 userId
    private String title;           // 도서 제목
    private String author;          // 저자
    private String publisher;       // 출판사
    private String isbn;            // ISBN (선택)
    private String category;        // 카테고리 (선택)
    private String reason;          // 요청 사유
    private int    copyCount;       // 요청 수량 (기본 1)
    private Date   requestDate;     // 요청일
    private Date   approvedDate;    // 승인일
    private String status;          // PENDING / APPROVED / REJECTED / ADDED
    private String librarianNote;   // 사서 메모 (반려 사유 등)
    private String addedBookId;     // 책 추가 완료 후 생성된 bookId
}