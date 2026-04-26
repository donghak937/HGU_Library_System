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

import java.util.Calendar;
import java.util.Date;

@Component
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository,
                                      BookCopyRepository bookCopyRepository,
                                      LoanRepository loanRepository,
                                      BookRepository bookRepository) {
        return args -> {

            // ════════════════════════════════════════════════
            // 날짜 헬퍼
            // ════════════════════════════════════════════════
            Calendar cal = Calendar.getInstance();

            // 과거 날짜 생성 헬퍼용 (대출일)
            cal.add(Calendar.DAY_OF_MONTH, -10); Date tenDaysAgo = cal.getTime();
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -20); Date twentyDaysAgo = cal.getTime();
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -5);  Date fiveDaysAgo = cal.getTime();

            // 반납 기한 (미래)
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 4);   Date dueIn4  = cal.getTime();
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 10);  Date dueIn10 = cal.getTime();
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 25);  Date dueIn25 = cal.getTime();

            // 반납 기한 (이미 지남 - 연체)
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -3);  Date overdue3  = cal.getTime();
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -8);  Date overdue8  = cal.getTime();

            // 정지 종료일
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);   Date suspEnd = cal.getTime();


            // ════════════════════════════════════════════════
            // 회원 5명
            // ════════════════════════════════════════════════

            // ✅ 대출 가능: user123 (한도 3, 현재 대출 1권)
            Member m1 = new Member();
            m1.setUserId("user123");
            m1.setMaxLoanLimit(3);
            m1.setLoanPeriod(14);
            m1.setSuspended(false);
            memberRepository.save(m1);

            // ✅ 대출 가능: hong_gildong (한도 3, 현재 대출 0권)
            Member m2 = new Member();
            m2.setUserId("hong_gildong");
            m2.setMaxLoanLimit(3);
            m2.setLoanPeriod(14);
            m2.setSuspended(false);
            memberRepository.save(m2);

            // ✅ 대출 가능: prof_lee (교수, 한도 5, 현재 대출 2권)
            Member m3 = new Member();
            m3.setUserId("prof_lee");
            m3.setMaxLoanLimit(5);
            m3.setLoanPeriod(30);
            m3.setSuspended(false);
            memberRepository.save(m3);

            // ❌ 대출 불가: limit_user (한도 3, 현재 대출 3권 → 한도 초과)
            Member m4 = new Member();
            m4.setUserId("limit_user");
            m4.setMaxLoanLimit(3);
            m4.setLoanPeriod(14);
            m4.setSuspended(false);
            memberRepository.save(m4);

            // ❌ 대출 불가: suspended_user (정지 중, 7일 후 해제)
            Member m5 = new Member();
            m5.setUserId("suspended_user");
            m5.setMaxLoanLimit(3);
            m5.setLoanPeriod(14);
            m5.setSuspended(true);
            m5.setSuspensionEndDate(suspEnd);
            memberRepository.save(m5);


            // ════════════════════════════════════════════════
            // 도서 30권 + 사본 (책당 1~3개)
            // ════════════════════════════════════════════════

            // ── 소설 ─────────────────────────────────────────

            // B-001 해리포터 (사본 3개)
            saveBook(bookRepository, "B-001", "해리포터와 마법사의 돌", "J.K. 롤링", "문학수첩", "9788983920677", "소설");
            saveCopy(bookCopyRepository, "C-001", "B-001", "BC-001", "대출중",   "1F-A01");
            saveCopy(bookCopyRepository, "C-002", "B-001", "BC-002", "대출가능", "1F-A01");
            saveCopy(bookCopyRepository, "C-003", "B-001", "BC-003", "대출가능", "1F-A01");

            // B-002 채식주의자 (사본 2개)
            saveBook(bookRepository, "B-002", "채식주의자", "한강", "창비", "9788936433598", "소설");
            saveCopy(bookCopyRepository, "C-004", "B-002", "BC-004", "대출가능", "1F-A02");
            saveCopy(bookCopyRepository, "C-005", "B-002", "BC-005", "대출중",   "1F-A02");

            // B-003 82년생 김지영 (사본 2개)
            saveBook(bookRepository, "B-003", "82년생 김지영", "조남주", "민음사", "9788937473135", "소설");
            saveCopy(bookCopyRepository, "C-006", "B-003", "BC-006", "대출가능", "1F-A03");
            saveCopy(bookCopyRepository, "C-007", "B-003", "BC-007", "대출가능", "1F-A03");

            // B-004 아몬드 (사본 1개)
            saveBook(bookRepository, "B-004", "아몬드", "손원평", "창비", "9788936434267", "소설");
            saveCopy(bookCopyRepository, "C-008", "B-004", "BC-008", "대출중", "1F-A04");

            // B-005 완전한 행복 (사본 1개)
            saveBook(bookRepository, "B-005", "완전한 행복", "정유정", "은행나무", "9791191071108", "소설");
            saveCopy(bookCopyRepository, "C-009", "B-005", "BC-009", "대출가능", "1F-A05");

            // B-006 누가 기침 소리를 내었는가 (사본 1개)
            saveBook(bookRepository, "B-006", "누가 기침 소리를 내었는가", "궁예", "마구니출판사", "9791100005809", "소설");
            saveCopy(bookCopyRepository, "C-010", "B-006", "BC-010", "대출가능", "1F-A06");

            // ── 컴퓨터공학 ──────────────────────────────────

            // B-007 객체지향 디자인 패턴 (사본 3개)
            saveBook(bookRepository, "B-007", "객체지향 디자인 패턴", "Erich Gamma 외", "프로텍미디어", "9791158390754", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-011", "B-007", "BC-011", "대출가능", "2F-C01");
            saveCopy(bookCopyRepository, "C-012", "B-007", "BC-012", "대출중",   "2F-C01");
            saveCopy(bookCopyRepository, "C-013", "B-007", "BC-013", "대출가능", "2F-C01");

            // B-008 Clean Code (사본 2개)
            saveBook(bookRepository, "B-008", "Clean Code", "Robert C. Martin", "인사이트", "9788966260959", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-014", "B-008", "BC-014", "대출중",   "2F-C02");
            saveCopy(bookCopyRepository, "C-015", "B-008", "BC-015", "대출가능", "2F-C02");

            // B-009 자바의 정석 (사본 3개)
            saveBook(bookRepository, "B-009", "자바의 정석", "남궁성", "도우출판", "9788994492032", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-016", "B-009", "BC-016", "대출가능", "2F-C03");
            saveCopy(bookCopyRepository, "C-017", "B-009", "BC-017", "대출가능", "2F-C03");
            saveCopy(bookCopyRepository, "C-018", "B-009", "BC-018", "대출중",   "2F-C03");

            // B-010 운영체제 (사본 2개)
            saveBook(bookRepository, "B-010", "운영체제", "Abraham Silberschatz", "퍼스트북", "9791185475219", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-019", "B-010", "BC-019", "대출가능", "2F-C04");
            saveCopy(bookCopyRepository, "C-020", "B-010", "BC-020", "대출가능", "2F-C04");

            // B-011 컴퓨터 네트워크 (사본 1개)
            saveBook(bookRepository, "B-011", "컴퓨터 네트워크", "James F. Kurose", "퍼스트북", "9791185475387", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-021", "B-011", "BC-021", "대출가능", "2F-C05");

            // B-012 알고리즘 문제 해결 전략 (사본 2개)
            saveBook(bookRepository, "B-012", "알고리즘 문제 해결 전략", "구종만", "인사이트", "9788966260546", "컴퓨터공학");
            saveCopy(bookCopyRepository, "C-022", "B-012", "BC-022", "대출중",   "2F-C06");
            saveCopy(bookCopyRepository, "C-023", "B-012", "BC-023", "대출가능", "2F-C06");

            // ── 역사 ─────────────────────────────────────────

            // B-013 사피엔스 (사본 3개)
            saveBook(bookRepository, "B-013", "사피엔스", "유발 하라리", "김영사", "9788934972464", "역사");
            saveCopy(bookCopyRepository, "C-024", "B-013", "BC-024", "대출가능", "1F-B01");
            saveCopy(bookCopyRepository, "C-025", "B-013", "BC-025", "대출중",   "1F-B01");
            saveCopy(bookCopyRepository, "C-026", "B-013", "BC-026", "대출가능", "1F-B01");

            // B-014 총균쇠 (사본 2개)
            saveBook(bookRepository, "B-014", "총균쇠", "재레드 다이아몬드", "문학사상사", "9788970128474", "역사");
            saveCopy(bookCopyRepository, "C-027", "B-014", "BC-027", "대출가능", "1F-B02");
            saveCopy(bookCopyRepository, "C-028", "B-014", "BC-028", "대출가능", "1F-B02");

            // B-015 조선왕조실록 (사본 1개)
            saveBook(bookRepository, "B-015", "조선왕조실록", "이성무", "살림", "9788952207135", "역사");
            saveCopy(bookCopyRepository, "C-029", "B-015", "BC-029", "대출가능", "1F-B03");

            // ── 과학 ─────────────────────────────────────────

            // B-016 이기적 유전자 (사본 2개)
            saveBook(bookRepository, "B-016", "이기적 유전자", "리처드 도킨스", "을유문화사", "9788932473901", "과학");
            saveCopy(bookCopyRepository, "C-030", "B-016", "BC-030", "대출가능", "1F-D01");
            saveCopy(bookCopyRepository, "C-031", "B-016", "BC-031", "대출중",   "1F-D01");

            // B-017 코스모스 (사본 2개)
            saveBook(bookRepository, "B-017", "코스모스", "칼 세이건", "사이언스북스", "9788983710956", "과학");
            saveCopy(bookCopyRepository, "C-032", "B-017", "BC-032", "대출가능", "1F-D02");
            saveCopy(bookCopyRepository, "C-033", "B-017", "BC-033", "대출가능", "1F-D02");

            // B-018 파인만의 물리학 강의 (사본 1개)
            saveBook(bookRepository, "B-018", "파인만의 물리학 강의", "리처드 파인만", "승산", "9788959408825", "과학");
            saveCopy(bookCopyRepository, "C-034", "B-018", "BC-034", "대출가능", "1F-D03");

            // ── 경영/경제 ────────────────────────────────────

            // B-019 린 스타트업 (사본 2개)
            saveBook(bookRepository, "B-019", "린 스타트업", "에릭 리스", "인사이트", "9788966260287", "경영");
            saveCopy(bookCopyRepository, "C-035", "B-019", "BC-035", "대출가능", "3F-E01");
            saveCopy(bookCopyRepository, "C-036", "B-019", "BC-036", "대출가능", "3F-E01");

            // B-020 제로 투 원 (사본 1개)
            saveBook(bookRepository, "B-020", "제로 투 원", "피터 틸", "한국경제신문", "9788947540728", "경영");
            saveCopy(bookCopyRepository, "C-037", "B-020", "BC-037", "대출중", "3F-E02");

            // B-021 넛지 (사본 2개)
            saveBook(bookRepository, "B-021", "넛지", "리처드 탈러", "리더스북", "9788990994288", "경영");
            saveCopy(bookCopyRepository, "C-038", "B-021", "BC-038", "대출가능", "3F-E03");
            saveCopy(bookCopyRepository, "C-039", "B-021", "BC-039", "대출가능", "3F-E03");

            // ── 철학 ─────────────────────────────────────────

            // B-022 소크라테스의 변명 (사본 1개)
            saveBook(bookRepository, "B-022", "소크라테스의 변명", "플라톤", "문예출판사", "9788931002119", "철학");
            saveCopy(bookCopyRepository, "C-040", "B-022", "BC-040", "대출가능", "3F-F01");

            // B-023 니체의 말 (사본 2개)
            saveBook(bookRepository, "B-023", "니체의 말", "프리드리히 니체", "삼호미디어", "9788977000476", "철학");
            saveCopy(bookCopyRepository, "C-041", "B-023", "BC-041", "대출가능", "3F-F02");
            saveCopy(bookCopyRepository, "C-042", "B-023", "BC-042", "대출중",   "3F-F02");

            // B-024 정의란 무엇인가 (사본 3개)
            saveBook(bookRepository, "B-024", "정의란 무엇인가", "마이클 샌델", "김영사", "9788934935346", "철학");
            saveCopy(bookCopyRepository, "C-043", "B-024", "BC-043", "대출가능", "3F-F03");
            saveCopy(bookCopyRepository, "C-044", "B-024", "BC-044", "대출가능", "3F-F03");
            saveCopy(bookCopyRepository, "C-045", "B-024", "BC-045", "대출중",   "3F-F03");

            // ── 자기계발 ─────────────────────────────────────

            // B-025 미라클 모닝 (사본 1개)
            saveBook(bookRepository, "B-025", "미라클 모닝", "할 엘로드", "한빛비즈", "9791162240144", "자기계발");
            saveCopy(bookCopyRepository, "C-046", "B-025", "BC-046", "대출가능", "3F-G01");

            // B-026 아주 작은 습관의 힘 (사본 2개)
            saveBook(bookRepository, "B-026", "아주 작은 습관의 힘", "제임스 클리어", "비즈니스북스", "9791165213312", "자기계발");
            saveCopy(bookCopyRepository, "C-047", "B-026", "BC-047", "대출가능", "3F-G02");
            saveCopy(bookCopyRepository, "C-048", "B-026", "BC-048", "대출가능", "3F-G02");

            // B-027 그릿 (사본 1개)
            saveBook(bookRepository, "B-027", "그릿", "앤절라 더크워스", "비즈니스북스", "9791165210502", "자기계발");
            saveCopy(bookCopyRepository, "C-049", "B-027", "BC-049", "대출중", "3F-G03");

            // ── 수학 ─────────────────────────────────────────

            // B-028 수학의 아름다움 (사본 2개)
            saveBook(bookRepository, "B-028", "수학의 아름다움", "오군", "인사이트", "9788966261840", "수학");
            saveCopy(bookCopyRepository, "C-050", "B-028", "BC-050", "대출가능", "2F-H01");
            saveCopy(bookCopyRepository, "C-051", "B-028", "BC-051", "대출가능", "2F-H01");

            // B-029 확률론적 사고 (사본 1개)
            saveBook(bookRepository, "B-029", "확률론적 사고", "이상엽", "사이언스북스", "9788983717191", "수학");
            saveCopy(bookCopyRepository, "C-052", "B-029", "BC-052", "대출가능", "2F-H02");

            // B-030 수학 귀신 (사본 2개)
            saveBook(bookRepository, "B-030", "수학 귀신", "한스 마그누스 엔첸스베르거", "비룡소", "9788949104195", "수학");
            saveCopy(bookCopyRepository, "C-053", "B-030", "BC-053", "대출가능", "2F-H03");
            saveCopy(bookCopyRepository, "C-054", "B-030", "BC-054", "대출가능", "2F-H03");


            // ════════════════════════════════════════════════
            // 대출 기록
            // ════════════════════════════════════════════════

            // user123: 1권 대출중 (해리포터 C-001)
            saveLoan(loanRepository, "L-001", "user123", "C-001", tenDaysAgo, dueIn4,  null,      "대출중");

            // hong_gildong: 반납 완료 기록 2개
            saveLoan(loanRepository, "L-002", "hong_gildong", "C-005", twentyDaysAgo, overdue8, twentyDaysAgo, "반납완료");
            saveLoan(loanRepository, "L-003", "hong_gildong", "C-014", twentyDaysAgo, overdue8, fiveDaysAgo,   "반납완료");

            // prof_lee: 2권 대출중
            saveLoan(loanRepository, "L-004", "prof_lee", "C-012", fiveDaysAgo,  dueIn25, null, "대출중");
            saveLoan(loanRepository, "L-005", "prof_lee", "C-031", tenDaysAgo,   dueIn10, null, "대출중");

            // limit_user: 3권 대출중 → 한도 초과 상태
            saveLoan(loanRepository, "L-006", "limit_user", "C-008", tenDaysAgo,  dueIn4,  null, "대출중");
            saveLoan(loanRepository, "L-007", "limit_user", "C-018", fiveDaysAgo, dueIn10, null, "대출중");
            saveLoan(loanRepository, "L-008", "limit_user", "C-022", fiveDaysAgo, dueIn10, null, "대출중");

            // suspended_user: 연체로 인해 정지된 기록 (반납완료지만 정지는 유지)
            saveLoan(loanRepository, "L-009", "suspended_user", "C-037", twentyDaysAgo, overdue3, fiveDaysAgo, "반납완료");

            // 추가 반납완료 기록들
            saveLoan(loanRepository, "L-010", "user123",     "C-020", twentyDaysAgo, overdue8, tenDaysAgo,   "반납완료");
            saveLoan(loanRepository, "L-011", "hong_gildong","C-045", twentyDaysAgo, overdue8, twentyDaysAgo,"반납완료");
            saveLoan(loanRepository, "L-012", "prof_lee",    "C-033", twentyDaysAgo, overdue8, fiveDaysAgo,  "반납완료");

        };
    }

    // ── 헬퍼 메서드 ─────────────────────────────────────────────────

    private void saveBook(BookRepository repo, String id, String title, String author,
                          String publisher, String isbn, String category) {
        Book b = new Book();
        b.setBookId(id);
        b.setTitle(title);
        b.setAuthor(author);
        b.setPublisher(publisher);
        b.setIsbn(isbn);
        b.setCategory(category);
        repo.save(b);
    }

    private void saveCopy(BookCopyRepository repo, String copyId, String bookId,
                          String barcode, String status, String location) {
        BookCopy c = new BookCopy();
        c.setCopyId(copyId);
        c.setBookId(bookId);
        c.setBarcode(barcode);
        c.setStatus(status);
        c.setLocation(location);
        repo.save(c);
    }

    private void saveLoan(LoanRepository repo, String loanId, String userId, String copyId,
                          Date loanDate, Date dueDate, Date returnDate, String status) {
        Loan l = new Loan();
        l.setLoanId(loanId);
        l.setUserId(userId);
        l.setCopyId(copyId);
        l.setLoanDate(loanDate);
        l.setDueDate(dueDate);
        l.setReturnDate(returnDate);
        l.setStatus(status);
        repo.save(l);
    }
}