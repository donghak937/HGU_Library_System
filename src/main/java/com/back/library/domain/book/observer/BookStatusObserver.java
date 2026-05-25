package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.BookCopy;

public interface BookStatusObserver {
    /**
     * 도서 상태 변경 이벤트를 처리하는 메서드
     * @param bookId 도서 식별 고유 ID
     * @param copy 상태가 변한 도서 사본 객체
     * @param oldStatus 이전 상태 (예: "대출중", "수선중", null)
     * @param newStatus 새로운 상태 (예: "대출가능", "폐기됨", "분실" 등)
     */
    void handleStatusChange(String bookId, BookCopy copy, String oldStatus, String newStatus);
}
