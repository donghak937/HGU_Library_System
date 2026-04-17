package com.back.library.domain.user.repository;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
