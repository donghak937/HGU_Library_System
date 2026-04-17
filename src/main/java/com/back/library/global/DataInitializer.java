package com.back.library.global;

import com.back.library.domain.book.entity.Book;
import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
import com.back.library.domain.book.repository.BookRepository;
import com.back.library.domain.book.repository.BookCopyRepository;
import com.back.library.domain.book.repository.LoanRepository;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 테스트용 초기 데이터 자동 주입기
 */
@Component
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository, BookCopyRepository bookCopyRepository,
            LoanRepository loanRepository, BookRepository bookRepository) {
        return args -> {
            // [기존] 반납 전용 데이터 유지
            Member member = new Member();
            member.setUserId("user123");
            member.setMaxLoanLimit(3);
            member.setLoanPeriod(14);
            member.setSuspended(false);
            memberRepository.save(member);

            BookCopy copy = new BookCopy();
            copy.setCopyId("book123");
            copy.setBarcode("BC-001");
            copy.setStatus("대출중");
            copy.setLocation("Shelf A");
            bookCopyRepository.save(copy);

            Loan loan = new Loan();
            loan.setLoanId("loan123");
            loan.setUserId("user123");
            loan.setCopyId("book123");
            loan.setLoanDate(new Date());
            loan.setStatus("대출중");
            loanRepository.save(loan);

            // [신규] 검색 테스트용 도서(Book) 데이터 투입
            Book book1 = new Book();
            book1.setBookId("B-001");
            book1.setTitle("해리포터와 마법사의 돌");
            book1.setAuthor("J.K. 롤링");
            book1.setPublisher("문학수첩");
            book1.setIsbn("9788983920677");
            book1.setCategory("소설");
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setBookId("B-002");
            book2.setTitle("누가 기침 소리를 내었는가");
            book2.setAuthor("궁예");
            book2.setPublisher("마구니출판사");
            book2.setIsbn("5809");
            book2.setCategory("역사");
            bookRepository.save(book2);

            Book book3 = new Book();
            book3.setBookId("B-003");
            book3.setTitle("객체지향 디자인 패턴");
            book3.setAuthor("김XX");
            book3.setPublisher("AI컴퓨터전자공학부");
            book3.setIsbn("5804");
            book3.setCategory("컴퓨터공학");
            bookRepository.save(book3);
        };
    }
}
