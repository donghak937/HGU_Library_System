package com.back.library.domain.book.strategy;

import com.back.library.domain.book.entity.Book;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoratorChainTest {

    @Test
    void decoratorCanWrapAnotherDecorator() {
        Book javaBook = book("B-001", "Java", "IT");
        Book gown = book("B-002", "Gown", "학위복");
        Book algorithmBook = book("B-003", "Algorithm", "IT");

        BookSearchStrategy baseStrategy = keyword -> List.of(javaBook, gown, algorithmBook);
        BookSearchStrategy chain = new TitleSortDecorator(
                new CategoryFilterDecorator(baseStrategy, "학위복", true)
        );

        List<Book> result = chain.search("anything");

        assertEquals(List.of(algorithmBook, javaBook), result);
    }

    private Book book(String id, String title, String category) {
        Book book = new Book();
        book.setBookId(id);
        book.setTitle(title);
        book.setCategory(category);
        return book;
    }
}
