package com.back.library.domain.book.observer;

import com.back.library.domain.book.entity.OverdueRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 연체 이벤트 Subject (Observer 패턴).
 * OverdueObserver를 구현한 모든 Spring Bean을 자동 주입받아 이벤트를 전파한다.
 * BookStatusSubject와 동일한 구조로 설계되었으며, 연체 도메인 이벤트 전용이다.
 */
@Component
@RequiredArgsConstructor
public class OverdueSubject {

    // Spring이 OverdueObserver를 구현하는 모든 Bean을 자동으로 리스트에 주입
    private final List<OverdueObserver> observers;

    /**
     * 등록된 모든 OverdueObserver에게 연체 발생 이벤트를 알림.
     * @param record 생성된 연체 기록
     */
    public void notifyObservers(OverdueRecord record) {
        for (OverdueObserver observer : observers) {
            observer.onOverdueOccurred(record);
        }
    }
}
