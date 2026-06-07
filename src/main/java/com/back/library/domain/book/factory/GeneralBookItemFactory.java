package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;
import org.springframework.stereotype.Component;

@Component
public class GeneralBookItemFactory implements LibraryItemFactory {

    @Override
    public Book createBook(BookRequest request, String bookId, String isbn, String category) {
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setIsbn(isbn);
        book.setCategory(category);
        return book;
    }

    @Override
    public BookCopy createCopy(String bookId, String copyId, int sequence) {
        BookCopy prototype = new BookCopy();
        prototype.setBookId(bookId);
        prototype.setStatus("대출가능");
        prototype.setLocation("신착도서 코너");

        BookCopy copy = prototype.copy();
        copy.setCopyId(copyId);
        copy.setBarcode("REQ-" + copyId);
        return copy;
    }
}
