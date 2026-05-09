package com.back.library.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    private String reservationId;

    private String bookId;    // 어떤 책을 예약했는지
    private String userId;    // 누가 예약했는지
    private Date requestDate; // 예약 요청일
    private int queueNumber;  // 대기 순번
    private String status;    // "대기중" | "취소됨" | "완료됨"
}