package com.back.library.domain.book.service;

import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.repository.*;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class BookBorrowProcessor extends BorrowProcessor {

    public BookBorrowProcessor(MemberRepository memberRepository,
                               BookRepository bookRepository,
                               BookCopyRepository bookCopyRepository,
                               LoanRepository loanRepository,
                               ReservationRepository reservationRepository) {
        super(memberRepository, bookRepository, bookCopyRepository, loanRepository, reservationRepository);
    }

    @Override
    protected void validateSpecificRules(Member member, String bookId) throws IllegalArgumentException {
        // 도서 전용 비즈니스 규칙이 검증될 곳 (현재는 없음)
    }

    @Override
    protected Date calculateDueDate(Member member) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, member.getLoanPeriod());
        return cal.getTime();
    }

    @Override
    protected void updateCopyStatus(BookCopy copy) {
        copy.setStatus("대출중");
        bookCopyRepository.save(copy);
    }

    @Override
    protected String getSuccessMessage() {
        return "대출이 완료되었습니다.";
    }
}
