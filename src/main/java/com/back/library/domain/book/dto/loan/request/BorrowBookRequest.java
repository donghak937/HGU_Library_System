package com.back.library.domain.book.dto.loan.request;

import org.antlr.v4.runtime.misc.NotNull;

public record BorrowBookRequest (
        @NotNull
        Long loanId
){
}
