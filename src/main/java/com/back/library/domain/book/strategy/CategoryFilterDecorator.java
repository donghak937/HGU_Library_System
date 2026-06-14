package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;

import java.util.List;

/**
 * BookSearchStrategy를 감싸 검색 결과를 특정 카테고리로 포함하거나 제외하는 데코레이터입니다.
 * delegate가 다른 데코레이터일 수도 있으므로 데코레이터 체인을 만들 수 있습니다.
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
