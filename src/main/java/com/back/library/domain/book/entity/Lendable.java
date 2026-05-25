package com.back.library.domain.book.entity;

/**
 * 대출 가능한 모든 물품(도서, 학위복 등)이 구현해야 하는 공통 인터페이스
 */
public interface Lendable {
    String getItemId();   // 물품 고유 ID (도서 ID 등)
    String getTitle();    // 물품명 (도서명 등)
    String getCategory(); // 물품 분류 (카테고리)
}
