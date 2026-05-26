package com.back.library.domain.book.service;

import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.repository.*;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class GownBorrowProcessor extends BorrowProcessor {

    public GownBorrowProcessor(MemberRepository memberRepository,
                               BookRepository bookRepository,
                               BookCopyRepository bookCopyRepository,
                               LoanRepository loanRepository,
                               ReservationRepository reservationRepository) {
        super(memberRepository, bookRepository, bookCopyRepository, loanRepository, reservationRepository);
    }

    @Override
    protected void validateSpecificRules(Member member, String bookId) throws IllegalArgumentException {
        // 포항 시민(pohang_user)은 학위복 대여 불가
        if ("pohang_user".equals(member.getUserId())) {
            throw new IllegalArgumentException("포항 지역 주민은 학위복 대여가 불가능합니다.");
        }
    }

    @Override
    protected Date calculateDueDate(Member member) {
        // 학위복은 대출 기한이 무조건 7일 고정
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        return cal.getTime();
    }

    @Override
    protected void updateCopyStatus(BookCopy copy) {
        // 학위복은 대여 처리 (DB 상에는 호환성을 위해 '대출중'으로 저장)
        copy.setStatus("대출중");
        bookCopyRepository.save(copy);
    }

    @Override
    protected String getSuccessMessage() {
        return "대여가 완료되었습니다.";
    }
}
