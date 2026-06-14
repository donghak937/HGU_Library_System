package com.back.library.domain.book.factory;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.BookRequest;

import java.util.List;

public interface LibraryItemFactory {

    LibraryItemSet createItemSet(BookRequest request, String bookId, String isbn, String category, List<String> copyIds);

    Book createBook(BookRequest request, String bookId, String isbn, String category);

    BookCopy createCopy(String bookId, String copyId, int sequence);
}
