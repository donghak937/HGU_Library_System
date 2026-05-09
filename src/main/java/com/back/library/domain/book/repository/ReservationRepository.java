package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // 특정 책의 대기 중인 예약 목록 (순번 순)
    List<Reservation> findByBookIdAndStatusOrderByQueueNumberAsc(String bookId, String status);

    // 특정 책의 대기 중인 예약 수
    long countByBookIdAndStatus(String bookId, String status);

    // 특정 유저가 특정 책을 이미 예약 중인지 확인
    Optional<Reservation> findByBookIdAndUserIdAndStatus(String bookId, String userId, String status);

    // 특정 유저의 예약 목록
    List<Reservation> findByUserIdAndStatus(String userId, String status);
}