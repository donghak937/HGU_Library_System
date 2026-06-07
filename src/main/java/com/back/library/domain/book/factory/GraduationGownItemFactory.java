package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;
import org.springframework.stereotype.Component;

@Component
public class GraduationGownItemFactory implements LibraryItemFactory {

    private static final String GOWN_CATEGORY = "학위복";

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
        prototype.setStatus("대출가능");
        prototype.setLocation("학위복 보관실");

        BookCopy copy = prototype.copy();
        copy.setCopyId(copyId);
        copy.setBarcode("GOWN-" + copyId);
        return copy;
    }
}
