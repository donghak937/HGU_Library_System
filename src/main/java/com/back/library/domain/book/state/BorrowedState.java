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
    public boolean canExtend(Loan loan, boolean hasReservation) {
        // 연체 중이면 불가
        if (loan.getDueDate() != null && loan.getDueDate().before(new Date())) {
            return false;
        }
        // 다음 예약자가 있으면 연장 불가
        if (hasReservation) {
            return false;
        }
        return true;
    }

    @Override
    public int calculateOverdueDays(Loan loan) {
        if (loan.getDueDate() == null || !loan.getDueDate().before(new Date())) return 0;
        long diff = new Date().getTime() - loan.getDueDate().getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }
}