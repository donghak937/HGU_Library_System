package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.BookCopy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogObserver implements BookStatusObserver {

    @Override
    public void handleStatusChange(String bookId, BookCopy copy, String oldStatus, String newStatus) {
        log.info("[AUDIT LOG] 도서 상태 변경 감지 - 도서 ID: {}, 사본 ID: {}, 바코드: {}, 상태 변경: {} -> {}",
                bookId, copy.getCopyId(), copy.getBarcode(), oldStatus, newStatus);
    }
}
