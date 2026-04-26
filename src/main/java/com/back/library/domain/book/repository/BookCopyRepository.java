package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {
    // 특정 bookId 중 대출가능한 사본 1개 찾기
    Optional<BookCopy> findFirstByBookIdAndStatus(String bookId, String status);
    // bookId로 해당 책의 모든 사본 조회
    List<BookCopy> findByBookId(String bookId);
}
