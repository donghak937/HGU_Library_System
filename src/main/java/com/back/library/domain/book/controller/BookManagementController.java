package com.back.library.domain.book.controller;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book/admin")
@RequiredArgsConstructor
public class BookManagementController {

    private final BookService bookService;

    // 도서 관리 페이지 뷰 매핑
    @GetMapping("/BookManagementUI")
    public String showBookManagementUI() {
        return "book/BookManagementUI";
    }

    // 모든 도서 조회
    @GetMapping("/allBooks")
    @ResponseBody
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // 도서 원형 등록 (addBook)
    @PostMapping("/addBook")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBook(@RequestBody Book book) {
        try {
            Book saved = bookService.addBook(book);
            return ResponseEntity.ok(Map.of("success", true, "message", "도서 원본 등록이 완료되었습니다.", "book", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 도서 원형 정보 수정 (updateBookInfo)
    @PostMapping("/updateInfo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBookInfo(@RequestParam String bookId, @RequestBody Book updatedInfo) {
        try {
            Book updated = bookService.updateBookInfo(bookId, updatedInfo);
            return ResponseEntity.ok(Map.of("success", true, "message", "도서 정보가 성공적으로 수정되었습니다.", "book", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 도서 사본 등록 (addCopy)
    @PostMapping("/addCopy")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBookCopy(@RequestParam String bookId, @RequestParam String location) {
        try {
            BookCopy copy = bookService.addBookCopy(bookId, location);
            return ResponseEntity.ok(Map.of("success", true, "message", "도서 사본이 성공적으로 등록되었습니다.", "copy", copy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 도서 사본 상태 변경 (updateBookStatus)
    @PostMapping("/updateCopyStatus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCopyStatus(@RequestParam String copyId, @RequestParam String status) {
        try {
            BookCopy updated = bookService.updateBookCopyStatus(copyId, status);
            return ResponseEntity.ok(Map.of("success", true, "message", "도서 사본 상태가 변경되었습니다.", "copy", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 도서 사본 폐기 (discardBook)
    @PostMapping("/discardCopy")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> discardBookCopy(@RequestParam String copyId) {
        try {
            BookCopy copy = bookService.discardBookCopy(copyId);
            return ResponseEntity.ok(Map.of("success", true, "message", "도서 사본이 정상적으로 폐기되었습니다.", "copy", copy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
