package com.back.library.domain.book.dto.loan.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BorrowBookRequest {
    private String memberId;
    private String bookId;
}