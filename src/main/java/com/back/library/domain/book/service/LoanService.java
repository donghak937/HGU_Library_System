package com.back.library.domain.book.service;


import com.back.library.domain.book.dto.loan.request.BorrowBookRequest;
import com.back.library.domain.book.dto.loan.response.BorrowBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    @Transactional
    public BorrowBookResponse createLoan(BorrowBookRequest request, Long currentUserId) {

        return null;
    }
}
