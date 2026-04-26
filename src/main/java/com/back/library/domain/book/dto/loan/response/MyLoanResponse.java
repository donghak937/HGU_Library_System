package com.back.library.domain.book.dto.loan.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class MyLoanResponse {
    private String loanId;
    private String bookId;
    private String bookTitle;
    private String author;
    private String barcode;
    private Date loanDate;
    private Date dueDate;
}
