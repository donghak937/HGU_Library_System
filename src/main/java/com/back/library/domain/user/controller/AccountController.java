package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.LoginRequest;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        Member member = memberRepository.findById(request.getUserId())
                .orElse(null);

        if (member == null) {
            return Map.of(
                    "success", false,
                    "message", "존재하지 않는 사용자입니다."
            );
        }

        if (!member.getPassword().equals(request.getPassword())) {
            return Map.of(
                    "success", false,
                    "message", "비밀번호가 틀렸습니다."
            );
        }

        return Map.of(
                "success", true,
                "message", "로그인 성공",
                "userId", member.getUserId(),
                "name", member.getName(),
                "role", member.getRole()
        );
    }
}