package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.book.repository.ReservationRepository;
import com.back.library.domain.book.service.LoanService;
import com.back.library.domain.book.state.BorrowedState;
import com.back.library.domain.book.state.LoanState;
import com.back.library.domain.book.state.OverdueState;
import com.back.library.domain.book.state.ReturnedState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;
    private final ReservationRepository reservationRepository;

    public LoanState getLoanState(Loan loan) {
        if (loan.getReturnDate() != null) return new ReturnedState();
        if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) return new OverdueState();
        return new BorrowedState();
    }

    @GetMapping("/ReturnBookUI")
    public String showReturnBookUI() {
        return "loan/ReturnBookUI";
    }

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

                bookCopyRepository.findById(loan.getCopyId()).ifPresent(copy -> {
                    copy.setStatus("대출가능");
                    bookCopyRepository.save(copy);
                });
                return true;
            }
        }
        return false;
    }

    @GetMapping("/myLoans")
    @ResponseBody
    public java.util.List<com.back.library.domain.book.dto.loan.response.MyLoanResponse> viewCurrentLoans(
            @RequestParam String memberId) {
        return loanService.getActiveLoans(memberId);
    }

    @PostMapping("/borrowBook")
    @ResponseBody
    public boolean borrowBook(@RequestBody BorrowBookRequest request) {
        BorrowBookResponse response = loanService.createLoan(request, null);
        return response.isSuccess();
    }

    /**
     * 대출 연장 처리 (extendLoan)
     * - State 패턴으로 연장 가능 여부 확인
     * - 예약자 있으면 연장 불가
     */
    @PostMapping("/extendLoan")
    @ResponseBody
    public Map<String, Object> extendLoan(@RequestParam String loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            return Map.of("success", false, "message", "존재하지 않는 대출입니다.");
        }
        Loan loan = loanOpt.get();

        // 해당 책에 다음 예약자가 있는지 확인
        boolean hasReservation = bookCopyRepository.findById(loan.getCopyId())
                .map(copy -> reservationRepository.countByBookIdAndStatus(copy.getBookId(), "대기중") > 0)
                .orElse(false);

        // State 패턴으로 연장 가능 여부 확인 (예약 여부 포함)
        LoanState state = getLoanState(loan);
        if (!state.canExtend(loan, hasReservation)) {
            String reason = hasReservation
                    ? "연장 불가: 다음 예약자가 있습니다."
                    : "연장 불가: 연체 중이거나 이미 반납된 도서입니다.";
            return Map.of("success", false, "message", reason);
        }

        if (loan.getExtensionCount() >= 1) {
            return Map.of("success", false, "message", "연장 불가: 이미 1회 연장하셨습니다.");
        }

        // 7일 연장
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