package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<BorrowBookResponse> borrowBook(
            @Valid @RequestBody BorrowBookRequest request
    ) {
        BorrowBookResponse response = loanService.createLoan(request, 1L);
        return ResponseEntity.ok(response);
    }
}
