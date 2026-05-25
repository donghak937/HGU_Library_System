package com.back.library.domain.book.dto.overdue.response;

import com.back.library.domain.book.entity.OverdueRecord;
import com.back.library.domain.user.entity.Member;
import lombok.Getter;

import java.util.Date;

/**
 * 연체 기록 응답 DTO (viewOverdueList, viewOverdueDetails).
 * Member 정보를 함께 받아 정지 관련 정보도 포함한다.
 */
@Getter
public class OverdueRecordResponse {

    private final String  overdueId;
    private final String  loanId;
    private final String  userId;
    private final Date    dueDate;            // 반납일 (연체 기준일)
    private final int     overdueDays;        // 연체일수
    private final int     suspendDays;        // 정지 일수 (오늘 기준 남은 정지 일수)
    private final boolean isSuspended;        // 정지 여부
    private final Date    suspensionEndDate;  // 정지 마감일

    public OverdueRecordResponse(OverdueRecord record, Date dueDate, Member member) {
        this.overdueId  = record.getOverdueId();
        this.loanId     = record.getLoanId();
        this.userId     = record.getUserId();
        this.dueDate    = dueDate;
        this.overdueDays = record.getOverdueDays();

        if (member != null) {
            this.isSuspended      = member.isSuspended();
            this.suspensionEndDate = member.getSuspensionEndDate();
            // 오늘부터 정지 마감일까지 남은 일수
            if (member.getSuspensionEndDate() != null
                    && member.getSuspensionEndDate().after(new Date())) {
                long diff = member.getSuspensionEndDate().getTime() - new Date().getTime();
                this.suspendDays = (int) (diff / (1000L * 60 * 60 * 24)) + 1;
            } else {
                this.suspendDays = 0;
            }
        } else {
            this.isSuspended      = false;
            this.suspensionEndDate = null;
            this.suspendDays      = 0;
        }
    }
}