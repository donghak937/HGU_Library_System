package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;

import java.util.List;

public interface BookSearchStrategy {
    List<Book> search(String keyword);
}
