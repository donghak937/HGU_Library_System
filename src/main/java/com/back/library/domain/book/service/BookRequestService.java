package com.back.library.domain.book.service;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.BookRepository;
import com.back.library.domain.book.repository.BookRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 교원/대학원생 도서 구입 요청 서비스.
 *
 * 상태 흐름:
 *   PENDING → APPROVED (approveRequest)
 *   PENDING → REJECTED (rejectRequest)
 *   APPROVED → ADDED   (addBookFromRequest) — Book + BookCopy 자동 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookRequestService {

    private final BookRequestRepository bookRequestRepository;
    private final BookRepository        bookRepository;
    private final BookCopyRepository    bookCopyRepository;

    // ── requestBook — 도서 구입 요청 ─────────────────────────
    @Transactional
    public BookRequest requestBook(String requesterId, String title, String author,
                                   String publisher, String isbn, String category,
                                   String reason, int copyCount) {
        BookRequest req = new BookRequest();
        req.setRequestId(UUID.randomUUID().toString());
        req.setRequesterId(requesterId);
        req.setTitle(title);
        req.setAuthor(author);
        req.setPublisher(publisher);
        req.setIsbn(isbn != null ? isbn : "");
        req.setCategory(category != null ? category : "기타");
        req.setReason(reason != null ? reason : "");
        req.setCopyCount(copyCount > 0 ? copyCount : 1);
        req.setRequestDate(new Date());
        req.setStatus("PENDING");
        return bookRequestRepository.save(req);
    }

    // ── 내 요청 목록 ──────────────────────────────────────────
    public List<BookRequest> getMyRequests(String requesterId) {
        return bookRequestRepository.findByRequesterIdOrderByRequestDateDesc(requesterId);
    }

    // ── 전체 요청 목록 (사서/관리자) ──────────────────────────
    public List<BookRequest> getAllRequests() {
        return bookRequestRepository.findAllByOrderByRequestDateDesc();
    }

    // ── approveRequest — 승인 ─────────────────────────────────
    @Transactional
    public BookRequest approveRequest(String requestId, String librarianNote) {
        BookRequest req = findById(requestId);
        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("검토중 상태인 요청만 승인할 수 있습니다.");
        }
        req.setStatus("APPROVED");
        req.setApprovedDate(new Date());
        req.setLibrarianNote(librarianNote != null ? librarianNote : "");
        log.info("[BookRequest] 승인 - requestId: {}, title: {}", requestId, req.getTitle());
        return bookRequestRepository.save(req);
    }

    // ── rejectRequest — 반려 ──────────────────────────────────
    @Transactional
    public BookRequest rejectRequest(String requestId, String librarianNote) {
        BookRequest req = findById(requestId);
        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("검토중 상태인 요청만 반려할 수 있습니다.");
        }
        req.setStatus("REJECTED");
        req.setLibrarianNote(librarianNote != null ? librarianNote : "");
        log.info("[BookRequest] 반려 - requestId: {}, title: {}", requestId, req.getTitle());
        return bookRequestRepository.save(req);
    }

    // ── addBookFromRequest — 책 도착 후 Book/BookCopy 자동 생성 ──
    @Transactional
    public BookRequest addBookFromRequest(String requestId, String isbn,
                                          String category, int copyCount) {
        BookRequest req = findById(requestId);
        if (!"APPROVED".equals(req.getStatus())) {
            throw new IllegalStateException("승인된 요청만 도서 등록이 가능합니다.");
        }

        // ISBN, category 최종 확정 (요청 시 입력값 우선, 없으면 기존값)
        String finalIsbn     = (isbn != null && !isbn.isBlank())
                ? isbn : req.getIsbn();
        String finalCategory = (category != null && !category.isBlank())
                ? category : req.getCategory();
        int    finalCount    = copyCount > 0 ? copyCount : req.getCopyCount();

        // ── Book 생성 ──────────────────────────────────────────
        String bookId = generateBookId();
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setPublisher(req.getPublisher());
        book.setIsbn(finalIsbn);
        book.setCategory(finalCategory);
        bookRepository.save(book);

        // ── BookCopy 생성 (copyCount 수만큼) ──────────────────
        for (int i = 1; i <= finalCount; i++) {
            String copyId = generateCopyId(bookId, i);
            BookCopy copy = new BookCopy();
            copy.setCopyId(copyId);
            copy.setBookId(bookId);
            copy.setBarcode("REQ-" + copyId);
            copy.setStatus("대출가능");
            copy.setLocation("신착도서 코너");
            bookCopyRepository.save(copy);
        }

        // ── 요청 상태 ADDED로 변경 ─────────────────────────────
        req.setStatus("ADDED");
        req.setAddedBookId(bookId);
        req.setIsbn(finalIsbn);
        req.setCategory(finalCategory);
        req.setCopyCount(finalCount);
        bookRequestRepository.save(req);

        log.info("[BookRequest] 도서 등록 완료 - requestId: {}, bookId: {}, copies: {}",
                requestId, bookId, finalCount);
        return req;
    }

    // ── private 헬퍼 ──────────────────────────────────────────
    private BookRequest findById(String requestId) {
        return bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 요청 ID: " + requestId));
    }

    private String generateBookId() {
        // 기존 Book의 최대 ID 기반으로 순번 생성
        List<Book> all = bookRepository.findAll();
        int maxNum = all.stream()
                .map(b -> b.getBookId().replaceAll("[^0-9]", ""))
                .filter(s -> !s.isEmpty())
                .mapToInt(s -> { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } })
                .max().orElse(0);
        return String.format("B-%03d", maxNum + 1);
    }

    private String generateCopyId(String bookId, int seq) {
        List<BookCopy> all = bookCopyRepository.findAll();
        int maxNum = all.stream()
                .map(c -> c.getCopyId().replaceAll("[^0-9]", ""))
                .filter(s -> !s.isEmpty())
                .mapToInt(s -> { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } })
                .max().orElse(0);
        return String.format("C-%03d", maxNum + seq);
    }
}