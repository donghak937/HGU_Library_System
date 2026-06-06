package com.back.library.domain.book.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundleLendableTest {

    @Test
    void testCompositePattern() {
        // Given
        Book book = new Book();
        book.setBookId("B-001");
        book.setTitle("토비의 스프링");
        book.setCategory("IT");

        GraduationGown gown = new GraduationGown("G-001", "학사복", "학사복", "L", "M");

        BundleLendable bundle = new BundleLendable("PKG-001", "졸업 도서 세트");

        // When (Empty Bundle)
        assertEquals("PKG-001", bundle.getItemId());
        assertEquals("졸업 도서 세트", bundle.getTitle());
        assertEquals("세트/번들", bundle.getCategory());
        assertTrue(bundle.getChildItems().isEmpty());

        // When (Adding items)
        bundle.add(book);
        bundle.add(gown);

        // Then
        assertEquals(2, bundle.getChildItems().size());
        assertEquals("졸업 도서 세트 [토비의 스프링, 학사복]", bundle.getTitle());

        // When (Removing items)
        bundle.remove(book);
        assertEquals(1, bundle.getChildItems().size());
        assertEquals("졸업 도서 세트 [학사복]", bundle.getTitle());
    }
}
