package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;

import java.util.List;

/**
 * BookSearchStrategy를 감싸서 특정 카테고리를 필터링(포함 또는 제외)하는 데커레이터 클래스
 */
public class CategoryFilterDecorator implements BookSearchStrategy {

    private final BookSearchStrategy delegate;
    private final String category;
    private final boolean exclude;

    public CategoryFilterDecorator(BookSearchStrategy delegate, String category, boolean exclude) {
        this.delegate = delegate;
        this.category = category;
        this.exclude = exclude;
    }

    @Override
    public List<Book> search(String keyword) {
        List<Book> results = delegate.search(keyword);
        if (results == null) {
            return List.of();
        }
        return results.stream()
                .filter(book -> exclude ? !category.equals(book.getCategory()) : category.equals(book.getCategory()))
                .toList();
    }
}
