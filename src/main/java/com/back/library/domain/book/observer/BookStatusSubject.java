package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.BookCopy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookStatusSubject {

    // Spring은 BookStatusObserver를 구현하는 모든 Bean들을 자동으로 리스트에 주입
    private final List<BookStatusObserver> observers;

    /**
     * 등록된 모든 옵저버들에게 상태 변경을 알림
     */
    public void notifyObservers(String bookId, BookCopy copy, String oldStatus, String newStatus) {
        for (BookStatusObserver observer : observers) {
            observer.handleStatusChange(bookId, copy, oldStatus, newStatus);
        }
    }
}
