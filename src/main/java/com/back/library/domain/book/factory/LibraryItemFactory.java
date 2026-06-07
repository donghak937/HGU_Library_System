package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;

public interface LibraryItemFactory {

    Book createBook(BookRequest request, String bookId, String isbn, String category);

    BookCopy createCopy(String bookId, String copyId, int sequence);
}
