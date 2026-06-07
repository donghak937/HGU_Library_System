package com.back.library.domain.book.controller;

import com.back.library.domain.book.entity.BookRequest;
import com.back.library.domain.book.service.BookRequestService;
import com.back.library.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 교원/대학원생 도서 구입 요청 Controller.
 *
 *  GET  /book/requestBook/RequestBookUI  → UI 페이지
 *  POST /book/requestBook                → 요청 제출 (PROFESSOR, FACULTY_GRADUATE)
 *  GET  /book/requestBook/my             → 내 요청 목록
 *  GET  /book/requestBook/all            → 전체 요청 목록 (사서/관리자)
 *  POST /book/requestBook/approve        → 승인 (사서/관리자)
 *  POST /book/requestBook/reject         → 반려 (사서/관리자)
 *  POST /book/requestBook/addBook        → 책 도착 후 Book/BookCopy 등록 (사서/관리자)
 */
@Controller
@RequestMapping("/book/requestBook")
@RequiredArgsConstructor
public class BookRequestController {

    private final BookRequestService bookRequestService;
    private final JwtUtil jwtUtil = JwtUtil.getInstance();

    @GetMapping("/RequestBookUI")
    public String showRequestBookUI() {
        return "book/RequestBookUI";
    }

    // ── requestBook — 도서 구입 요청 ─────────────────────────
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> requestBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "1") int copyCount,
            HttpServletRequest request) {
        try {
            String token    = jwtUtil.extractToken(request.getHeader("Authorization"));
            String role     = jwtUtil.getRole(token);
            String username = jwtUtil.getUsername(token);

            if (!"PROFESSOR".equals(role) && !"FACULTY_GRADUATE".equals(role)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "교원 또는 대학원생만 도서 구입 요청이 가능합니다."));
            }

            BookRequest req = bookRequestService.requestBook(
                    username, title, author, publisher, isbn, category, reason, copyCount);
            return ResponseEntity.ok(Map.of(
                    "success",   true,
                    "message",   "도서 구입 요청이 완료되었습니다.",
                    "requestId", req.getRequestId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ── 내 요청 목록 ──────────────────────────────────────────
    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<List<BookRequest>> getMyRequests(HttpServletRequest request) {
        String token    = jwtUtil.extractToken(request.getHeader("Authorization"));
        String username = jwtUtil.getUsername(token);
        return ResponseEntity.ok(bookRequestService.getMyRequests(username));
    }

    // ── 전체 요청 목록 (사서/관리자) ──────────────────────────
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Object> getAllRequests(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request.getHeader("Authorization"));
        String role  = jwtUtil.getRole(token);
        if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
        }
        return ResponseEntity.ok(bookRequestService.getAllRequests());
    }

    // ── approve — 승인 ────────────────────────────────────────
    @PostMapping("/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveRequest(
            @RequestParam String requestId,
            @RequestParam(required = false) String librarianNote,
            HttpServletRequest request) {
        try {
            String role = jwtUtil.getRole(jwtUtil.extractToken(request.getHeader("Authorization")));
            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
            }
            BookRequest req = bookRequestService.approveRequest(requestId, librarianNote);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "요청이 승인되었습니다.",
                    "status",  req.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ── reject — 반려 ─────────────────────────────────────────
    @PostMapping("/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectRequest(
            @RequestParam String requestId,
            @RequestParam(required = false) String librarianNote,
            HttpServletRequest request) {
        try {
            String role = jwtUtil.getRole(jwtUtil.extractToken(request.getHeader("Authorization")));
            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
            }
            BookRequest req = bookRequestService.rejectRequest(requestId, librarianNote);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "요청이 반려되었습니다.",
                    "status",  req.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ── addBook — 책 도착 후 Book/BookCopy 자동 등록 ──────────
    @PostMapping("/addBook")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBookFromRequest(
            @RequestParam String requestId,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int copyCount,
            HttpServletRequest request) {
        try {
            String role = jwtUtil.getRole(jwtUtil.extractToken(request.getHeader("Authorization")));
            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
            }
            BookRequest req = bookRequestService.addBookFromRequest(requestId, isbn, category, copyCount);
            return ResponseEntity.ok(Map.of(
                    "success",     true,
                    "message",     "도서가 성공적으로 등록되었습니다.",
                    "bookId",      req.getAddedBookId(),
                    "title",       req.getTitle(),
                    "copyCount",   req.getCopyCount()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}