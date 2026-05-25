package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.OverdueRecord;

/**
 * 연체 이벤트 Observer 인터페이스 (Observer 패턴).
 * BookStatusObserver와 독립적으로, 연체 도메인 전용 이벤트를 처리한다.
 */
public interface OverdueObserver {
    /**
     * 연체 이벤트 발생 시 호출되는 메서드.
     * @param record 생성된 연체 기록
     */
    void onOverdueOccurred(OverdueRecord record);
}
