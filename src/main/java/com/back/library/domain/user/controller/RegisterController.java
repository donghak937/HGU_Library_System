package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.RegisterRequest;
import com.back.library.domain.user.entity.Account;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.factory.AccountFactory;
import com.back.library.domain.user.repository.AccountRepository;
import com.back.library.domain.user.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class RegisterController {

    private final AccountRepository accountRepository;

    private final MemberRepository memberRepository;

    // 회원가입 UI
    @GetMapping("/RegisterUI")
    public String showRegisterUI() {

        return "account/registerUI";
    }

    // 회원가입 처리
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(
            @RequestBody RegisterRequest request
    ) {

        if (
                request.getUsername() == null
                ||
                request.getUsername().trim().isEmpty()
                ||
                request.getPassword() == null
                ||
                request.getPassword().trim().isEmpty()
        ) {

            return Map.of(
                    "success", false,
                    "message",
                    "아이디와 비밀번호는 필수입니다."
            );
        }
        // 아이디 중복 검사
        if (accountRepository
                .findByUsername(request.getUsername())
                .isPresent()) {

            return Map.of(
                    "success", false,
                    "message", "이미 존재하는 아이디입니다."
            );
        }

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        // 마지막 accountId 조회
        String maxId =
                accountRepository.findMaxAccountId();

        int nextNumber = 1;

        // 기존 값이 있으면 +1
        if (maxId != null) {

            nextNumber =
                    Integer.parseInt(
                            maxId.substring(2)
                    ) + 1;
        }

        // A-001 형식 생성
        String accountId =
                String.format(
                        "A-%03d",
                        nextNumber
                );

        // Factory Method Pattern 사용
        Account account =
                AccountFactory.createCitizenAccount(

                        accountId,

                        request.getUsername(),

                        encoder.encode(
                                request.getPassword()
                        )
                );

        // 저장
        accountRepository.save(account);

        // 시민 회원 생성
        Member member = new Member();

        member.setUserId(
                request.getUsername()
        );

        // 시민 정책
        member.setMaxLoanLimit(1);

        member.setLoanPeriod(7);

        member.setSuspended(false);

        memberRepository.save(member);

        return Map.of(
                "success", true,
                "message", "회원가입 성공"
        );
    }
}