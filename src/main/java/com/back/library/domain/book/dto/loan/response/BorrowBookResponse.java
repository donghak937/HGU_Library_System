package com.back.library.domain.book.dto.loan.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BorrowBookResponse {
    private boolean success;
    private String message;
    private String loanId;
}