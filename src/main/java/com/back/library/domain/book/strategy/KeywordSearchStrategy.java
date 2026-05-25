package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KeywordSearchStrategy implements BookSearchStrategy {

    private final BookRepository bookRepository;

    @Override
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }
}
