package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Reservation;
import com.back.library.domain.book.repository.BookRepository;
import com.back.library.domain.book.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationObserver implements BookStatusObserver {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    @Override
    public void handleStatusChange(String bookId, BookCopy copy, String oldStatus, String newStatus) {
        // 도서 사본 상태가 "대출가능"으로 변경되었을 때 예약 신청 목록에서 대기 중인 1순위 예약자 조회
        if ("대출가능".equals(newStatus)) {
            List<Reservation> activeReservations = reservationRepository
                    .findByBookIdAndStatusOrderByQueueNumberAsc(bookId, "대기중");

            if (!activeReservations.isEmpty()) {
                Reservation firstReservation = activeReservations.get(0);
                
                String bookTitle = bookRepository.findById(bookId)
                        .map(Book::getTitle)
                        .orElse("알 수 없는 도서");

                // 실제 서비스에서는 이메일/SMS/알림톡 등으로 발송될 메시지 시뮬레이션
                log.info("[NOTIFICATION] 예약 알림 발송 완료 - 수신자 ID: {}, 내용: 예약하신 도서 [{}] (바코드: {})가 대출 가능한 상태입니다.", 
                        firstReservation.getUserId(), bookTitle, copy.getBarcode());
            }
        }
    }
}
