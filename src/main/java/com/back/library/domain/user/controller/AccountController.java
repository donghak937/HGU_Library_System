package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.LoginRequest;
import com.back.library.domain.user.entity.Account;
import com.back.library.domain.user.repository.AccountRepository;
import com.back.library.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    // 로그인 UI
    @GetMapping("/loginUI")
    public String showLoginUI() {
        return "account/loginUI";
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        Account account = accountRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (account == null) {
            return Map.of("success", false, "message", "존재하지 않는 계정입니다.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), account.getPasswordHash())) {
            return Map.of("success", false, "message", "비밀번호가 틀렸습니다.");
        }

        if (!account.getStatus().equals("ACTIVE")) {
            return Map.of("success", false, "message", "사용할 수 없는 계정입니다.");
        }

        //Access Token 생성 (짧게)
        String accessToken = jwtUtil.createAccessToken(
                account.getAccountId(),
                account.getUsername(),
                account.getStatus()
        );

        //Refresh Token 생성 (길게)
        String refreshToken = jwtUtil.createRefreshToken(account.getAccountId());

        //DB 저장
        account.updateRefreshToken(refreshToken);
        accountRepository.save(account);

        return Map.of(
                "success", true,
                "message", "로그인 성공",
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "accountId", account.getAccountId(),
                "username", account.getUsername(),
                "status", account.getStatus()
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    @ResponseBody
    public Map<String, Object> logout() {
        return Map.of(
                "success", true,
                "message", "로그아웃 성공"
        );
    }

    // accessToken 재발급
    @PostMapping("/refresh")
    @ResponseBody
    public Map<String, Object> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");

        if (refreshToken == null) {
            return Map.of("success", false, "message", "refresh token이 없습니다.");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return Map.of("success", false, "message", "refresh 토큰 만료");
        }

        String accountId = jwtUtil.getAccountId(refreshToken);

        Account account = accountRepository.findById(accountId)
                .orElse(null);

        if (account == null || !refreshToken.equals(account.getRefreshToken())) {
            return Map.of("success", false, "message", "유효하지 않은 refresh token");
        }

        String newAccessToken = jwtUtil.createAccessToken(
                account.getAccountId(),
                account.getUsername(),
                account.getStatus()
        );

        return Map.of(
                "success", true,
                "accessToken", newAccessToken
        );
    }
}