package com.back.library.domain.book.service;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.dto.loan.response.MyLoanResponse;
import com.back.library.domain.book.entity.*;
import com.back.library.domain.book.repository.*;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import com.back.library.domain.book.state.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    private final BookBorrowProcessor bookBorrowProcessor;
    private final GownBorrowProcessor gownBorrowProcessor;

    public List<MyLoanResponse> getActiveLoans(String memberId) {
        List<Loan> activeLoans = loanRepository.findByUserIdAndStatus(memberId, "대출중");
        List<MyLoanResponse> result = new ArrayList<>();

        for (Loan loan : activeLoans) {
            Optional<BookCopy> copyOpt = bookCopyRepository.findById(loan.getCopyId());
            if (copyOpt.isEmpty()) continue;
            BookCopy copy = copyOpt.get();

            Optional<Book> bookOpt = bookRepository.findById(copy.getBookId());
            if (bookOpt.isEmpty()) continue;
            Book book = bookOpt.get();

            // 해당 책에 대기 중인 예약이 있는지 확인
            boolean hasReservation = reservationRepository
                    .countByBookIdAndStatus(book.getBookId(), "대기중") > 0;

            int overdueDays = getLoanState(loan).calculateOverdueDays(loan);

            result.add(new MyLoanResponse(
                    loan.getLoanId(),
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    copy.getBarcode(),
                    loan.getLoanDate(),
                    loan.getDueDate(),
                    loan.getExtensionCount(),
                    overdueDays,
                    hasReservation
            ));
        }
        return result;
    }

    @Transactional
    public BorrowBookResponse createLoan(BorrowBookRequest request, Long currentUserId) {
        String bookId = request.getBookId();

        boolean isGown = bookRepository.findById(bookId)
                .map(book -> "학위복".equals(book.getCategory()))
                .orElse(false);

        if (isGown) {
            return gownBorrowProcessor.borrow(request.getMemberId(), bookId);
        } else {
            return bookBorrowProcessor.borrow(request.getMemberId(), bookId);
        }
    }

    private LoanState getLoanState(Loan loan) {
        if (loan.getReturnDate() != null) return new ReturnedState();
        if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) return new OverdueState();
        return new BorrowedState();
    }
}