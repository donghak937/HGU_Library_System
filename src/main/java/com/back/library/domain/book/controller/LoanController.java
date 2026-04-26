package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.book.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.Date;
import java.util.Optional;

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
            
            if ("대출중".equals(loan.getStatus())) {
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
    public java.util.List<com.back.library.domain.book.dto.loan.response.MyLoanResponse> getMyLoans(@RequestParam String memberId) {
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
}
