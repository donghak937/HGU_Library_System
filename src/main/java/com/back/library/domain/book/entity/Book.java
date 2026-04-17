package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 도서 원형 데이터 엔티티
 */
@Entity
@Table(name = "book")
@NoArgsConstructor
@Getter
@Setter
public class Book {
    
    @Id
    private String bookId; // 도서 고유 번호
    
    private String title; // 도서 제목
    private String author; // 저자
    private String publisher; // 출판사
    private String isbn; // 국제표준도서번호
    private String category; // 카테고리 (예: "소설", "IT")
}
