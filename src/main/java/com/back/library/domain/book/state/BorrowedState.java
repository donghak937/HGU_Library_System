package com.back.library.domain.book.state;

import com.back.library.domain.book.entity.Loan;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BorrowedState implements LoanState {
    @Override
    public boolean canReturn(Loan loan) {
        return true;
    }

    @Override
    public boolean canExtend(Loan loan) {
        return loan.getDueDate() == null || !loan.getDueDate().before(new Date());
    }

    @Override
    public int calculateOverdueDays(Loan loan) {
        if (loan.getDueDate() == null || !loan.getDueDate().before(new Date())) {
            return 0;
        }

        long diff = new Date().getTime() - loan.getDueDate().getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }
}
