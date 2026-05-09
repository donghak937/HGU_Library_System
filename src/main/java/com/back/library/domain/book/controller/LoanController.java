package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.book.service.LoanService;
import com.back.library.domain.book.state.BorrowedState;
import com.back.library.domain.book.state.LoanState;
import com.back.library.domain.book.state.OverdueState;
import com.back.library.domain.book.state.ReturnedState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.Date;
import java.util.Optional;
import com.back.library.domain.user.repository.MemberRepository;
import java.util.Calendar;
import java.util.Map;

/**
 * 도서 반납 컨트롤러
 */
@Controller
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;
    private final MemberRepository memberRepository;

    public LoanState getLoanState(Loan loan) {
        if (loan.getReturnDate() != null) {
            return new ReturnedState();
        }

        if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) {
            return new OverdueState();
        }

        return new BorrowedState();
    }

    /**
     * 반납 UI 화면 렌더링
     */
    @GetMapping("/ReturnBookUI")
    public String showReturnBookUI() { 
        return "loan/ReturnBookUI"; 
    }

    /**
     * 책 반납 처리
     */
    @PostMapping("/returnBook")
    @ResponseBody
    public boolean returnBook(@RequestParam String loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            
            if (getLoanState(loan).canReturn(loan)) {
                loan.setStatus("반납완료");
                loan.setReturnDate(new Date());
                loanRepository.save(loan);

                Optional<BookCopy> copyOpt = bookCopyRepository.findById(loan.getCopyId());
                if (copyOpt.isPresent()) {
                    BookCopy copy = copyOpt.get();
                    copy.setStatus("대출가능");
                    bookCopyRepository.save(copy);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 특정 유저의 대출 중인 도서 목록 조회
     */
    @GetMapping("/myLoans")
    @ResponseBody
    public java.util.List<com.back.library.domain.book.dto.loan.response.MyLoanResponse> viewCurrentLoans(@RequestParam String memberId) {
        return loanService.getActiveLoans(memberId);
    }

    /**
     * 도서 대출 처리
     */
    @PostMapping("/borrowBook")
    @ResponseBody
    public boolean borrowBook(@RequestBody BorrowBookRequest request) {
        BorrowBookResponse response = loanService.createLoan(request, null);
        return response.isSuccess();
    }

    /**
     * 대출 연장 처리 (extendLoan)
     * - checkExtensionEligibility: State 패턴의 canExtend()로 확인
     * - updateDueDate: 7일 연장
     */
    @PostMapping("/extendLoan")
    @ResponseBody
    public Map<String, Object> extendLoan(@RequestParam String loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            return Map.of("success", false, "message", "존재하지 않는 대출입니다.");
        }

        Loan loan = loanOpt.get();

        // checkExtensionEligibility — State 패턴 활용
        LoanState state = getLoanState(loan);
        if (!state.canExtend(loan)) {
            return Map.of("success", false, "message", "연장 불가: 연체 중이거나 이미 반납된 도서입니다.");
        }

        // 최대 연장 횟수 확인 (1회)
        if (loan.getExtensionCount() >= 1) {
            return Map.of("success", false, "message", "연장 불가: 이미 1회 연장하셨습니다.");
        }

        // updateDueDate — 7일 연장
        Calendar cal = Calendar.getInstance();
        cal.setTime(loan.getDueDate());
        cal.add(Calendar.DAY_OF_MONTH, 7);
        loan.setDueDate(cal.getTime());
        loan.setExtensionCount(loan.getExtensionCount() + 1);
        loanRepository.save(loan);

        return Map.of(
                "success", true,
                "message", "대출이 7일 연장되었습니다.",
                "newDueDate", loan.getDueDate().toString()
        );
    }
}
