package com.back.library.domain.book.dto.loan.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private String reservationId;
    private String bookId;
    private String bookTitle;
    private Date requestDate;
    private int queueNumber;
    private String status;
}