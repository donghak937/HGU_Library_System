package com.back.library.domain.book.service;

import com.back.library.domain.book.dto.overdue.response.OverdueRecordResponse;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.entity.OverdueRecord;
import com.back.library.domain.book.observer.OverdueSubject;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.book.repository.OverdueRecordRepository;
import com.back.library.domain.book.state.BorrowedState;
import com.back.library.domain.book.state.LoanState;
import com.back.library.domain.book.state.OverdueState;
import com.back.library.domain.book.state.ReturnedState;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 연체 관련 기능을 담당하는 서비스.
 *
 * [사용되는 디자인 패턴]
 *  - State 패턴  : getLoanState()로 기존 LoanState 구현체를 활용,
 *                  calculateOverdueDays() 위임으로 연체일수를 계산한다.
 *  - Observer 패턴: runOverdueScan() 완료 후 OverdueSubject.notifyObservers()를 호출하여
 *                  OverdueNotificationObserver에 이벤트를 전파한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OverdueService {

    private final LoanRepository          loanRepository;
    private final MemberRepository        memberRepository;
    private final OverdueRecordRepository overdueRecordRepository;
    private final OverdueSubject          overdueSubject;

    // ──────────────────────────────────────────────────────
    // [자동 스케줄러] 매일 자정 실행
    // ──────────────────────────────────────────────────────
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoDetectOverdueLoans() {
        log.info("[OVERDUE SCHEDULER] 매일 자정 연체 스캔 실행");
        scanAndProcess();
    }

    /**
     * 앱 시작 시 즉시 1회 실행.
     * ApplicationReadyEvent는 CommandLineRunner(DataInitializer) 완료 후 발생하므로
     * 데이터가 모두 준비된 상태에서 스캔이 실행된다.
     * Self-invocation 문제를 피하기 위해 내부 로직을 직접 실행한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void runOnStartup() {
        log.info("[OVERDUE SCHEDULER] 앱 시작 시 연체 스캔 즉시 실행");
        scanAndProcess();
    }

    /**
     * 연체 스캔 핵심 로직 (내부 전용 — 트랜잭션 컨텍스트 내에서 호출).
     * - 신규 연체: 연체 기록 생성 + 연체일수만큼 즉시 정지 (1일 연체도 정지)
     * - 기존 연체: 연체일수 갱신 + 하루 증가 시 정지 1일 추가
     */
    private void scanAndProcess() {
        List<Loan> overdueLoans = loanRepository.findAllOverdueLoans();
        log.info("[OVERDUE SCHEDULER] 스캔 시작 - 대상 {}건", overdueLoans.size());

        for (Loan loan : overdueLoans) {
            int overdueDays = getLoanState(loan).calculateOverdueDays(loan);
            if (overdueDays <= 0) continue;

            Optional<OverdueRecord> existing =
                    overdueRecordRepository.findByLoanId(loan.getLoanId());

            if (existing.isEmpty()) {
                // ── 신규 연체 기록 생성 ──
                OverdueRecord record = new OverdueRecord();
                record.setOverdueId(UUID.randomUUID().toString());
                record.setLoanId(loan.getLoanId());
                record.setUserId(loan.getUserId());
                record.setDetectedAt(new Date());
                record.setOverdueDays(overdueDays);
                record.setPenaltyApplied(false);
                record.setSuspendApplied(true);
                OverdueRecord saved = overdueRecordRepository.save(record);

                // 1일 연체부터 즉시 정지 — 연체일수만큼 정지 기한 설정
                extendSuspensionInternal(loan.getUserId(), overdueDays);

                overdueSubject.notifyObservers(saved);
                log.info("[OVERDUE SCHEDULER] 신규 연체 + 즉시 정지 {}일 - loanId: {}, userId: {}",
                        overdueDays, loan.getLoanId(), loan.getUserId());
            } else {
                // ── 기존 연체 갱신 — 하루 늘 때마다 정지 1일 추가 ──
                OverdueRecord record = existing.get();
                int prevDays = record.getOverdueDays();
                record.setOverdueDays(overdueDays);
                overdueRecordRepository.save(record);

                if (overdueDays > prevDays) {
                    extendSuspensionInternal(loan.getUserId(), 1);
                    log.info("[OVERDUE SCHEDULER] 연체 +1일 → 정지 +1일 - loanId: {}, userId: {}, 연체: {}일",
                            loan.getLoanId(), loan.getUserId(), overdueDays);
                }
            }
        }
        log.info("[OVERDUE SCHEDULER] 스캔 완료");
    }

    // ──────────────────────────────────────────────────────
    // viewOverdueList — 연체 목록 조회
    // ──────────────────────────────────────────────────────
    public List<OverdueRecordResponse> viewOverdueList(String userId) {
        List<OverdueRecord> records = (userId != null && !userId.isBlank())
                ? overdueRecordRepository.findByUserId(userId)
                : overdueRecordRepository.findAll();

        List<OverdueRecordResponse> result = new ArrayList<>();
        for (OverdueRecord r : records) {
            Loan loan = loanRepository.findById(r.getLoanId()).orElse(null);
            Member member = memberRepository.findById(r.getUserId()).orElse(null);
            Date dueDate = (loan != null) ? loan.getDueDate() : null;
            result.add(new OverdueRecordResponse(r, dueDate, member));
        }
        return result;
    }

    // ──────────────────────────────────────────────────────
    // viewOverdueDetails — 연체 상세 조회
    // ──────────────────────────────────────────────────────
    public OverdueRecordResponse viewOverdueDetails(String overdueId) {
        OverdueRecord record = overdueRecordRepository.findById(overdueId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 연체 기록 ID: " + overdueId));
        Loan loan = loanRepository.findById(record.getLoanId()).orElse(null);
        Member member = memberRepository.findById(record.getUserId()).orElse(null);
        Date dueDate = (loan != null) ? loan.getDueDate() : null;
        return new OverdueRecordResponse(record, dueDate, member);
    }

    // ──────────────────────────────────────────────────────
    // applyLatePenalty — 사서가 원하는 일수만큼 정지 기한 연장
    // ──────────────────────────────────────────────────────
    @Transactional
    public OverdueRecordResponse applyLatePenalty(String overdueId, int days) {
        OverdueRecord record = overdueRecordRepository.findById(overdueId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 연체 기록 ID: " + overdueId));

        if (days <= 0) {
            throw new IllegalArgumentException("연장 일수는 1일 이상이어야 합니다.");
        }

        extendSuspensionInternal(record.getUserId(), days);
        record.setPenaltyApplied(true);
        OverdueRecord saved = overdueRecordRepository.save(record);

        Loan loan = loanRepository.findById(record.getLoanId()).orElse(null);
        Member member = memberRepository.findById(record.getUserId()).orElse(null);
        return new OverdueRecordResponse(saved,
                loan != null ? loan.getDueDate() : null, member);
    }

    // ──────────────────────────────────────────────────────
    // cancelSuspension — 정지 해지
    //   회원의 isSuspended = false, suspensionEndDate = null 로 초기화한다.
    // ──────────────────────────────────────────────────────
    @Transactional
    public OverdueRecordResponse cancelSuspension(String overdueId) {
        OverdueRecord record = overdueRecordRepository.findById(overdueId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 연체 기록 ID: " + overdueId));

        Member member = memberRepository.findById(record.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 회원 ID: " + record.getUserId()));

        member.setSuspended(false);
        member.setSuspensionEndDate(null);
        memberRepository.save(member);

        log.info("[OVERDUE] 정지 해지 - userId: {}", record.getUserId());

        Loan loan = loanRepository.findById(record.getLoanId()).orElse(null);
        return new OverdueRecordResponse(record,
                loan != null ? loan.getDueDate() : null, member);
    }

    // ──────────────────────────────────────────────────────
    // private 헬퍼
    // ──────────────────────────────────────────────────────

    /**
     * 회원의 대출 정지 기한을 days일만큼 연장한다.
     * 기존 정지 종료일이 미래이면 그 날짜에서 추가, 아니면 오늘부터 시작.
     * 반드시 트랜잭션 컨텍스트 내에서 호출해야 한다.
     */
    private void extendSuspensionInternal(String userId, int days) {
        memberRepository.findById(userId).ifPresent(member -> {
            Calendar cal = Calendar.getInstance();
            if (member.getSuspensionEndDate() != null
                    && member.getSuspensionEndDate().after(new Date())) {
                cal.setTime(member.getSuspensionEndDate());
            }
            cal.add(Calendar.DAY_OF_MONTH, days);
            member.setSuspended(true);
            member.setSuspensionEndDate(cal.getTime());
            memberRepository.save(member);
            log.info("[OVERDUE] 정지 기한 연장 - userId: {}, +{}일, 마감일: {}",
                    userId, days, cal.getTime());
        });
    }

    private LoanState getLoanState(Loan loan) {
        if (loan.getReturnDate() != null) return new ReturnedState();
        if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) return new OverdueState();
        return new BorrowedState();
    }
}