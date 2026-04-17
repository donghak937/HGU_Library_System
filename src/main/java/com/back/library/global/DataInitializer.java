package com.back.library.global;

import com.back.library.domain.book.entity.BookCopy;
import com.back.library.domain.book.entity.Loan;
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
    public CommandLineRunner initData(MemberRepository memberRepository, BookCopyRepository bookCopyRepository, LoanRepository loanRepository) {
        return args -> {
            // 1. 회원 데이터 생성
            Member member = new Member();
            member.setUserId("user123");
            member.setMaxLoanLimit(3);
            member.setLoanPeriod(14);
            member.setSuspended(false);
            memberRepository.save(member);

            // 2. 누군가 이미 빌려간 상태의 도서 사본 데이터 생성 (대출중)
            BookCopy copy = new BookCopy();
            copy.setCopyId("book123");
            copy.setBarcode("BC-001");
            copy.setStatus("대출중"); // 반납 테스트를 위해 "대출중"으로 설정
            copy.setLocation("Shelf A");
            bookCopyRepository.save(copy);

            // 3. 빌려간 책에 대한 대출(Loan) 기록 데이터 생성
            Loan loan = new Loan();
            loan.setLoanId("loan123"); // 테스트용 대출 기록 ID
            loan.setUserId("user123");
            loan.setCopyId("book123");
            loan.setLoanDate(new Date());
            loan.setStatus("대출중");
            loanRepository.save(loan);
        };
    }
}
