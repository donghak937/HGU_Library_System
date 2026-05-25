package com.back.library.domain.book.service;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.BookRepository;
import com.back.library.domain.book.observer.BookStatusSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookStatusSubject bookStatusSubject;

    // 모든 도서 원본 목록 조회
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // 도서 원형 추가 (addBook)
    @Transactional
    public Book addBook(Book book) {
        if (book.getBookId() == null || book.getBookId().isEmpty()) {
            book.setBookId(generateNextBookId());
        }
        return bookRepository.save(book);
    }

    // 도서 정보 수정 (updateBookInfo)
    @Transactional
    public Book updateBookInfo(String bookId, Book updatedInfo) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서 고유 번호입니다: " + bookId));
        
        book.setTitle(updatedInfo.getTitle());
        book.setAuthor(updatedInfo.getAuthor());
        book.setPublisher(updatedInfo.getPublisher());
        book.setIsbn(updatedInfo.getIsbn());
        book.setCategory(updatedInfo.getCategory());
        
        return bookRepository.save(book);
    }

    // 도서 사본 추가 (addCopy)
    @Transactional
    public BookCopy addBookCopy(String bookId, String location) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다. ID: " + bookId));

        BookCopy copy = new BookCopy();
        copy.setCopyId(generateNextCopyId());
        copy.setBookId(bookId);
        copy.setBarcode(generateNextBarcode());
        copy.setStatus("대출가능");
        copy.setLocation(location);

        BookCopy savedCopy = bookCopyRepository.save(copy);
        
        // 옵저버 알림 (신규 등록 시 "등록됨" -> "대출가능")
        bookStatusSubject.notifyObservers(bookId, savedCopy, "등록됨", "대출가능");

        return savedCopy;
    }

    // 도서 상태 업데이트 (updateBookStatus)
    @Transactional
    public BookCopy updateBookCopyStatus(String copyId, String newStatus) {
        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서 사본입니다. ID: " + copyId));

        String oldStatus = copy.getStatus();
        if (!oldStatus.equals(newStatus)) {
            copy.setStatus(newStatus);
            BookCopy updatedCopy = bookCopyRepository.save(copy);

            // 상태가 변했을 때 옵저버들에게 알림 발행
            bookStatusSubject.notifyObservers(copy.getBookId(), updatedCopy, oldStatus, newStatus);
            return updatedCopy;
        }
        return copy;
    }

    // 도서 사본 폐기 (discardBook)
    @Transactional
    public BookCopy discardBookCopy(String copyId) {
        return updateBookCopyStatus(copyId, "폐기됨");
    }

    private String generateNextBookId() {
        List<Book> books = bookRepository.findAll();
        int maxNum = 0;
        for (Book book : books) {
            String id = book.getBookId();
            if (id != null && id.startsWith("B-")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric formats (e.g. UUID)
                }
            }
        }
        return String.format("B-%03d", maxNum + 1);
    }

    private String generateNextCopyId() {
        List<BookCopy> copies = bookCopyRepository.findAll();
        int maxNum = 0;
        for (BookCopy copy : copies) {
            String id = copy.getCopyId();
            if (id != null && id.startsWith("C-")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric formats (e.g. UUID)
                }
            }
        }
        return String.format("C-%03d", maxNum + 1);
    }

    private String generateNextBarcode() {
        List<BookCopy> copies = bookCopyRepository.findAll();
        int maxNum = 0;
        for (BookCopy copy : copies) {
            String barcode = copy.getBarcode();
            if (barcode != null && barcode.startsWith("BC-")) {
                try {
                    int num = Integer.parseInt(barcode.substring(3));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric formats (e.g. UUID)
                }
            }
        }
        return String.format("BC-%03d", maxNum + 1);
    }
}
