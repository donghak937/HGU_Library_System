package com.back.library.domain.book.state;

import com.back.library.domain.book.entity.Loan;

public interface LoanState {
    boolean canReturn(Loan loan);

    boolean canExtend(Loan loan);

    int calculateOverdueDays(Loan loan);
}
