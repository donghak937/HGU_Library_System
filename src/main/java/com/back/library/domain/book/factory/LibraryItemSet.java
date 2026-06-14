package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;

import java.util.List;

/**
 * Abstract Factory가 생성하는 관련 객체군입니다.
 * Book과 그 Book에 속한 BookCopy 목록을 하나의 제품군 세트로 묶습니다.
 */
public class LibraryItemSet {

    private final Book book;
    private final List<BookCopy> copies;

    public LibraryItemSet(Book book, List<BookCopy> copies) {
        this.book = book;
        this.copies = copies;
    }

    public Book getBook() {
        return book;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }
}
