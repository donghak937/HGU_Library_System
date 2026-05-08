package com.back.library.domain.book.state;

import com.back.library.domain.book.entity.Loan;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OverdueState implements LoanState {
    @Override
    public boolean canReturn(Loan loan) {
        return true;
    }

    @Override
    public boolean canExtend(Loan loan) {
        return false;
    }

    @Override
    public int calculateOverdueDays(Loan loan) {
        if (loan.getDueDate() == null) {
            return 0;
        }

        long diff = new Date().getTime() - loan.getDueDate().getTime();
        return Math.max(0, (int) TimeUnit.MILLISECONDS.toDays(diff));
    }
}
