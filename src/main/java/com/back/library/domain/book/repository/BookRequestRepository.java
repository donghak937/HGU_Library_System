package com.back.library.domain.book.repository;

import com.back.library.domain.book.entity.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, String> {
    List<BookRequest> findByRequesterIdOrderByRequestDateDesc(String requesterId);
    List<BookRequest> findAllByOrderByRequestDateDesc();
    List<BookRequest> findByStatus(String status);
}