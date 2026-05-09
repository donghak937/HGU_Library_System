package com.back.library.domain.book.state;

import com.back.library.domain.book.entity.Loan;

public class ReturnedState implements LoanState {

    @Override
    public boolean canReturn(Loan loan) { return false; }

    @Override
    public boolean canExtend(Loan loan, boolean hasReservation) { return false; }

    @Override
    public int calculateOverdueDays(Loan loan) { return 0; }
}