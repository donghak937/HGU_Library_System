package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;

import java.util.Comparator;
import java.util.List;

/**
 * 다른 BookSearchStrategy 또는 데코레이터를 감싼 뒤 검색 결과를 제목순으로 정렬하는 데코레이터입니다.
 */
public class TitleSortDecorator implements BookSearchStrategy {

    private final BookSearchStrategy delegate;

    public TitleSortDecorator(BookSearchStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Book> search(String keyword) {
        List<Book> results = delegate.search(keyword);
        if (results == null) {
            return List.of();
        }
        return results.stream()
                .sorted(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareTo)))
                .toList();
    }
}
