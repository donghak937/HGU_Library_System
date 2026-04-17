package com.back.library.domain.book.controller;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 도서 검색 전담 컨트롤러
 */
@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    /**
     * 검색 UI 화면 렌더링
     */
    @GetMapping("/SearchBookUI")
    public String showSearchBookUI() {
        return "book/SearchBookUI";
    }

    /**
     * 키워드(제목/저자)로 도서 검색 (JSON 반환)
     */
    @GetMapping("/searchBooks")
    @ResponseBody
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }

    /**
     * 카테고리로 도서 검색 (JSON 반환)
     */
    @GetMapping("/searchBooksByCategory")
    @ResponseBody
    public List<Book> searchBooksByCategory(@RequestParam String category) {
        return bookRepository.findByCategory(category);
    }
}
