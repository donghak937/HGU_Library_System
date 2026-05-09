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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

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

            int overdueDays = 0;
            if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) {
                long diff = new Date().getTime() - loan.getDueDate().getTime();
                overdueDays = (int) TimeUnit.MILLISECONDS.toDays(diff);
            }

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
        String memberId = request.getMemberId();
        String bookId   = request.getBookId();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) return new BorrowBookResponse(false, "존재하지 않는 회원입니다.", null);
        Member member = memberOpt.get();

        if (member.isSuspended()) {
            if (member.getSuspensionEndDate() == null || new Date().before(member.getSuspensionEndDate())) {
                return new BorrowBookResponse(false, "대출 정지 상태입니다.", null);
            }
            member.setSuspended(false);
            member.setSuspensionEndDate(null);
            memberRepository.save(member);
        }

        long activeLoans = loanRepository.countByUserIdAndStatus(memberId, "대출중");
        if (activeLoans >= member.getMaxLoanLimit()) {
            return new BorrowBookResponse(false, "대출 한도를 초과했습니다.", null);
        }

        Optional<BookCopy> copyOpt = bookCopyRepository.findFirstByBookIdAndStatus(bookId, "대출가능");
        if (copyOpt.isEmpty()) {
            return new BorrowBookResponse(false, "대출 가능한 사본이 없습니다.", null);
        }
        BookCopy bookCopy = copyOpt.get();

        Calendar cal = Calendar.getInstance();
        Date loanDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, member.getLoanPeriod());
        Date dueDate = cal.getTime();

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID().toString());
        loan.setUserId(memberId);
        loan.setCopyId(bookCopy.getCopyId());
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus("대출중");
        loanRepository.save(loan);

        bookCopy.setStatus("대출중");
        bookCopyRepository.save(bookCopy);

        // 해당 유저의 이 책 예약이 있으면 완료 처리
        reservationRepository.findByBookIdAndUserIdAndStatus(bookId, memberId, "대기중")
                .ifPresent(r -> {
                    r.setStatus("완료됨");
                    reservationRepository.save(r);
                });

        return new BorrowBookResponse(true, "대출이 완료되었습니다.", loan.getLoanId());
    }
}