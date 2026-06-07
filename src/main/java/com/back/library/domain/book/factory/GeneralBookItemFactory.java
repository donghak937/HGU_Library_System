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
        BookCopy copy = new BookCopy();
        copy.setCopyId(copyId);
        copy.setBookId(bookId);
        copy.setBarcode("REQ-" + copyId);
        copy.setStatus("대출가능");
        copy.setLocation("신착도서 코너");
        return copy;
    }
}
