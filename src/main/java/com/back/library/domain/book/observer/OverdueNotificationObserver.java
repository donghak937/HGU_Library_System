package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.OverdueRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 연체 알림 발송 Observer 구현체 (sendOverdueNotification).
 * OverdueObserver를 구현하여 연체 기록 생성 시 사용자에게 알림을 발송한다.
 * 실제 서비스에서는 이메일/SMS/알림톡으로 대체된다.
 */
@Component
@Slf4j
public class OverdueNotificationObserver implements OverdueObserver {

    @Override
    public void onOverdueOccurred(OverdueRecord record) {
        log.info("[OVERDUE NOTIFICATION] 연체 알림 발송 완료 - 수신자 ID: {}, 대출 ID: {}, " +
                        "연체일수: {}일, " +
                        "내용: 대출하신 도서가 {}일 연체되었습니다. 빠른 반납 부탁드립니다. " +
                        "연체일수에 따라 대출 정지 기한이 연장될 수 있습니다.",
                record.getUserId(),
                record.getLoanId(),
                record.getOverdueDays(),
                record.getOverdueDays());
    }
}