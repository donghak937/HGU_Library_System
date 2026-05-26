package com.back.library.domain.book.service;

import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.*;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;

import java.util.Date;
import java.util.UUID;

public abstract class BorrowProcessor {

    protected final MemberRepository memberRepository;
    protected final BookRepository bookRepository;
    protected final BookCopyRepository bookCopyRepository;
    protected final LoanRepository loanRepository;
    protected final ReservationRepository reservationRepository;

    protected BorrowProcessor(MemberRepository memberRepository,
                              BookRepository bookRepository,
                              BookCopyRepository bookCopyRepository,
                              LoanRepository loanRepository,
                              ReservationRepository reservationRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    // Template Method (final)
    public final BorrowBookResponse borrow(String memberId, String bookId) {
        try {
            // 1. 공통 회원 검증
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            
            if (member.isSuspended()) {
                if (member.getSuspensionEndDate() == null || new Date().before(member.getSuspensionEndDate())) {
                    return new BorrowBookResponse(false, "대출 정지 상태입니다.", null);
                }
                // 정지 기간이 지난 경우 정지 해제
                member.setSuspended(false);
                member.setSuspensionEndDate(null);
                memberRepository.save(member);
            }

            // 2. 공통 한도 검증
            long activeLoans = loanRepository.countByUserIdAndStatus(memberId, "대출중");
            if (activeLoans >= member.getMaxLoanLimit()) {
                return new BorrowBookResponse(false, "대출 한도를 초과했습니다.", null);
            }

            // 3. 서브클래스별 상세 검증 (Hook)
            validateSpecificRules(member, bookId);

            // 4. 공통 사본 조회
            BookCopy bookCopy = bookCopyRepository.findFirstByBookIdAndStatus(bookId, "대출가능")
                    .orElseThrow(() -> new IllegalArgumentException("대출 가능한 사본이 없습니다."));

            // 5. 서브클래스별 반납 기한 계산 (Hook)
            Date dueDate = calculateDueDate(member);

            // 6. 공통 대출 처리 및 사본 상태 변경
            Loan loan = new Loan();
            loan.setLoanId(UUID.randomUUID().toString());
            loan.setUserId(memberId);
            loan.setCopyId(bookCopy.getCopyId());
            loan.setLoanDate(new Date());
            loan.setDueDate(dueDate);
            loan.setStatus("대출중");
            loanRepository.save(loan);

            // 7. 서브클래스별 사본 상태 변경 (Hook)
            updateCopyStatus(bookCopy);

            // 8. 공통 예약 정리
            reservationRepository.findByBookIdAndUserIdAndStatus(bookId, memberId, "대기중")
                    .ifPresent(r -> {
                        r.setStatus("완료됨");
                        reservationRepository.save(r);
                    });

            return new BorrowBookResponse(true, getSuccessMessage(), loan.getLoanId());
        } catch (IllegalArgumentException e) {
            return new BorrowBookResponse(false, e.getMessage(), null);
        }
    }

    // Hook Methods to be implemented by subclasses
    protected abstract void validateSpecificRules(Member member, String bookId) throws IllegalArgumentException;
    protected abstract Date calculateDueDate(Member member);
    protected abstract void updateCopyStatus(BookCopy copy);
    protected abstract String getSuccessMessage();
}
