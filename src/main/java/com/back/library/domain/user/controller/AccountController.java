package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.LoginRequest;
import com.back.library.domain.user.entity.Account;
import com.back.library.domain.user.repository.AccountRepository;
import com.back.library.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/loginUI")
    public String showLoginUI() {
        return "account/loginUI";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> ssoLogin(@RequestBody LoginRequest request) {

        Account account = accountRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (account == null) {
            return Map.of("success", false, "message", "존재하지 않는 계정입니다.");
        }

        // 입력된 비밀번호와 DB에 저장된 해시된 비밀번호를 비교하기 위해 BCryptPasswordEncoder 사용        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), account.getPasswordHash())) {
            return Map.of("success", false, "message", "비밀번호가 틀렸습니다.");
        }
        if (!account.getStatus().equals("ACTIVE")) {
            return Map.of("success", false, "message", "사용할 수 없는 계정입니다.");
        }

        String token = jwtUtil.createToken(
                account.getAccountId(),
                account.getUsername(),
                account.getStatus()
        );

        return Map.of(
                "success", true,
                "message", "SSO 로그인 성공",
                "token", token,
                "accountId", account.getAccountId(),
                "username", account.getUsername(),
                "status", account.getStatus()
        );
    }
    @PostMapping("/logout")
    @ResponseBody
    public Map<String, Object> logout() {
        return Map.of(
                "success", true,
                "message", "로그아웃 성공"
        );
    }
}