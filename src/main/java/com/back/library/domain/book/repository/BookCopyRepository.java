package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
