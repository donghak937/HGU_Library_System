package com.back.library.domain.book.service;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BorrowBookResponse createLoan(BorrowBookRequest request, Long currentUserId) {
        String memberId = request.getMemberId();
        String bookId   = request.getBookId();   // ← copyId에서 변경

        // 1. 회원 조회
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return new BorrowBookResponse(false, "존재하지 않는 회원입니다.", null);
        }
        Member member = memberOpt.get();

        // 2. 정지 여부 확인
        if (member.isSuspended()) {
            if (member.getSuspensionEndDate() == null
                    || new Date().before(member.getSuspensionEndDate())) {
                return new BorrowBookResponse(false, "대출 정지 상태입니다.", null);
            }
            member.setSuspended(false);
            member.setSuspensionEndDate(null);
            memberRepository.save(member);
        }

        // 3. 대출 한도 확인
        long activeLoans = loanRepository.countByUserIdAndStatus(memberId, "대출중");
        if (activeLoans >= member.getMaxLoanLimit()) {
            return new BorrowBookResponse(false, "대출 한도를 초과했습니다.", null);
        }

        // 4. 대출 가능한 사본 자동 선택
        Optional<BookCopy> copyOpt = bookCopyRepository.findFirstByBookIdAndStatus(bookId, "대출가능");
        if (copyOpt.isEmpty()) {
            return new BorrowBookResponse(false, "대출 가능한 사본이 없습니다.", null);
        }
        BookCopy bookCopy = copyOpt.get();

        // 5. 반납 기한 계산
        Calendar cal = Calendar.getInstance();
        Date loanDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, member.getLoanPeriod());
        Date dueDate = cal.getTime();

        // 6. Loan 생성
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID().toString());
        loan.setUserId(memberId);
        loan.setCopyId(bookCopy.getCopyId());
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus("대출중");
        loanRepository.save(loan);

        // 7. BookCopy 상태 변경
        bookCopy.setStatus("대출중");
        bookCopyRepository.save(bookCopy);

        return new BorrowBookResponse(true, "대출이 완료되었습니다.", loan.getLoanId());
    }
}