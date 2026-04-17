package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    // 제목이나 저자 이름에 검색어가 포함된 책 찾기
    List<Book> findByTitleContainingOrAuthorContaining(String titleKeyword, String authorKeyword);
    
    // 특정 카테고리의 책 다 찾기
    List<Book> findByCategory(String category);
}
