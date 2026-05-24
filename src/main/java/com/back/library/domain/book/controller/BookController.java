package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.loan.response.ReservationResponse;
import com.back.library.domain.book.entity.*;
import com.back.library.domain.book.repository.*;
import com.back.library.domain.book.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final KeywordSearchStrategy keywordSearchStrategy;
    private final CategorySearchStrategy categorySearchStrategy;
    private BookSearchStrategy searchStrategy;
    private final LoanRepository loanRepository;

    public void setSearchStrategy(BookSearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    @GetMapping("/SearchBookUI")
    public String showSearchBookUI() { return "book/SearchBookUI"; }

    @GetMapping("/searchBooks")
    @ResponseBody
    public List<Book> searchBooks(@RequestParam String keyword) {
        setSearchStrategy(keywordSearchStrategy);
        return searchStrategy.search(keyword);
    }

    @GetMapping("/searchBooksByCategory")
    @ResponseBody
    public List<Book> searchBooksByCategory(@RequestParam String category) {
        setSearchStrategy(categorySearchStrategy);
        return searchStrategy.search(category);
    }

    @GetMapping("/details")
    @ResponseBody
    public ResponseEntity<Book> viewItemDetail(@RequestParam String bookId) {
        return bookRepository.findById(bookId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/copies")
    @ResponseBody
    public List<BookCopy> getCopiesByBookId(@RequestParam String bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    /**
     * 도서 예약 (reserveBook)
     * - 대출 가능한 사본이 없거나 모두 대출 중일 때만 예약 가능
     * - 동일 책 중복 예약 불가
     */
    @PostMapping("/reserveBook")
    @ResponseBody
    public Map<String, Object> reserveBook(@RequestParam String memberId,
                                           @RequestParam String bookId) {
        // 1. 이미 예약 중인지 확인
        Optional<Reservation> existing = reservationRepository
                .findByBookIdAndUserIdAndStatus(bookId, memberId, "대기중");
        if (existing.isPresent()) {
            return Map.of("success", false, "message", "이미 예약 중인 도서입니다.");
        }

        List<BookCopy> copies = bookCopyRepository.findByBookId(bookId);
        boolean alreadyBorrowed = copies.stream()
                .anyMatch(copy -> loanRepository
                        .findByUserIdAndStatus(memberId, "대출중")
                        .stream()
                        .anyMatch(loan -> loan.getCopyId().equals(copy.getCopyId())));
        if (alreadyBorrowed) {
            return Map.of("success", false, "message", "이미 대출 중인 도서는 예약할 수 없습니다.");
        }

        // 2. 대출 가능한 사본이 있으면 예약 불필요
        Optional<BookCopy> availableCopy = bookCopyRepository
                .findFirstByBookIdAndStatus(bookId, "대출가능");
        if (availableCopy.isPresent()) {
            return Map.of("success", false, "message", "현재 대출 가능한 사본이 있습니다. 바로 대출하세요.");
        }

        // 3. 예약 생성
        long queueNumber = reservationRepository.countByBookIdAndStatus(bookId, "대기중") + 1;

        Reservation reservation = new Reservation();
        reservation.setReservationId(UUID.randomUUID().toString());
        reservation.setBookId(bookId);
        reservation.setUserId(memberId);
        reservation.setRequestDate(new Date());
        reservation.setQueueNumber((int) queueNumber);
        reservation.setStatus("대기중");
        reservationRepository.save(reservation);

        return Map.of("success", true,
                "message", queueNumber + "번째 예약이 완료되었습니다.",
                "queueNumber", queueNumber);
    }

    /**
     * 예약 취소 (cancelReservation)
     */
    @PostMapping("/cancelReservation")
    @ResponseBody
    public Map<String, Object> cancelReservation(@RequestParam String reservationId) {
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        if (resOpt.isEmpty()) {
            return Map.of("success", false, "message", "존재하지 않는 예약입니다.");
        }
        Reservation reservation = resOpt.get();
        if (!"대기중".equals(reservation.getStatus())) {
            return Map.of("success", false, "message", "취소할 수 없는 상태입니다.");
        }
        reservation.setStatus("취소됨");
        reservationRepository.save(reservation);
        return Map.of("success", true, "message", "예약이 취소되었습니다.");
    }

    /**
     * 내 예약 목록 조회
     */
    @GetMapping("/myReservations")
    @ResponseBody
    public List<ReservationResponse> getMyReservations(@RequestParam String memberId) {
        List<Reservation> reservations = reservationRepository
                .findByUserIdAndStatus(memberId, "대기중");
        List<ReservationResponse> result = new ArrayList<>();

        for (Reservation r : reservations) {
            String title = bookRepository.findById(r.getBookId())
                    .map(Book::getTitle).orElse("알 수 없음");
            result.add(new ReservationResponse(
                    r.getReservationId(), r.getBookId(), title,
                    r.getRequestDate(), r.getQueueNumber(), r.getStatus()
            ));
        }
        return result;
    }
}