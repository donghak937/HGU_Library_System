package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.LoginRequest;
import com.back.library.domain.user.entity.Account;
import com.back.library.domain.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    /**
     * 로그인 UI 화면 렌더링
     */
    @GetMapping("/loginUI")
    public String showLoginUI() {
        return "account/loginUI";
    }

    /**
     * 로그인 처리 API
     */
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        Account account = accountRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (account == null) {
            return Map.of(
                    "success", false,
                    "message", "존재하지 않는 계정입니다."
            );
        }

        if (!account.getPasswordHash().equals(request.getPassword())) {
            return Map.of(
                    "success", false,
                    "message", "비밀번호가 틀렸습니다."
            );
        }

        if (!account.getStatus().equals("ACTIVE")) {
            return Map.of(
                    "success", false,
                    "message", "사용할 수 없는 계정입니다."
            );
        }

        return Map.of(
                "success", true,
                "message", "로그인 성공",
                "accountId", account.getAccountId(),
                "username", account.getUsername(),
                "ssoEnabled", account.isSsoEnabled(),
                "status", account.getStatus()
        );
    }
}