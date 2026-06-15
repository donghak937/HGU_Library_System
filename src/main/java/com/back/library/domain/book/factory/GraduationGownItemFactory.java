package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GraduationGownItemFactory implements LibraryItemFactory {

    private static final String GOWN_CATEGORY = "\uD559\uC704\uBCF5";

    @Override
    public LibraryItemSet createItemSet(BookRequest request, String bookId, String isbn, String category, List<String> copyIds) {
        Book book = createBook(request, bookId, isbn, category);
        List<BookCopy> copies = new ArrayList<>();

        for (int i = 0; i < copyIds.size(); i++) {
            copies.add(createCopy(bookId, copyIds.get(i), i + 1));
        }

        return new LibraryItemSet(book, copies);
    }

    @Override
    public Book createBook(BookRequest request, String bookId, String isbn, String category) {
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setIsbn(isbn);
        book.setCategory(GOWN_CATEGORY);
        return book;
    }

    @Override
    public BookCopy createCopy(String bookId, String copyId, int sequence) {
        BookCopy prototype = new BookCopy();
        prototype.setBookId(bookId);
        prototype.setStatus("\uB300\uCD9C\uAC00\uB2A5");
        prototype.setLocation("\uD559\uC704\uBCF5 \uBCF4\uAD00\uC2E4");

        BookCopy copy = prototype.copy();
        copy.setCopyId(copyId);
        copy.setBarcode("GOWN-" + copyId);
        return copy;
    }
}
