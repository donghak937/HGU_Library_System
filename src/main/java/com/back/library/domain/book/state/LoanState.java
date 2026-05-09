package com.back.library.domain.book.state;

import com.back.library.domain.book.entity.Loan;

public interface LoanState {
    boolean canReturn(Loan loan);

    boolean canExtend(Loan loan, boolean hasReservation);

    int calculateOverdueDays(Loan loan);
}
