package com.back.library.domain.user.controller;

import com.back.library.domain.user.dto.request.LoginRequest;
import com.back.library.domain.user.entity.Account;
import com.back.library.domain.user.entity.Member;
import com.back.library.domain.user.repository.AccountRepository;
import com.back.library.domain.user.repository.MemberRepository;
import com.back.library.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.Cookie;


@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    // Singleton 객체 사용
    private final JwtUtil jwtUtil = JwtUtil.getInstance();

    // 회원 상태 조회 (정지 상태 포함)
    @GetMapping("/memberStatus")
    @ResponseBody
    public Map<String, Object> getMemberStatus(@RequestParam String userId) {
        return memberRepository.findById(userId)
                .map(member -> {
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("success", true);
                    result.put("suspended", member.isSuspended());
                    result.put("suspensionEndDate", member.getSuspensionEndDate());
                    result.put("maxLoanLimit", member.getMaxLoanLimit());
                    result.put("loanPeriod", member.getLoanPeriod());
                    return result;
                })
                .orElse(Map.of("success", false, "message", "존재하지 않는 회원입니다."));
    }

    // 로그인 UI
    @GetMapping("/loginUI")
    public String showLoginUI() {
        return "account/loginUI";
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        Account account = accountRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (account == null) {
            return Map.of(
                    "success", false,
                    "message", "존재하지 않는 계정입니다."
            );
        }

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        if (!encoder.matches(
                request.getPassword(),
                account.getPasswordHash()
        )) {

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

        // Access Token
        String accessToken =
                jwtUtil.createAccessToken(
                        account.getAccountId(),
                        account.getUsername(),
                        account.getStatus(),
                        account.getRole()
                );

        // Refresh Token
        String refreshToken =
                jwtUtil.createRefreshToken(
                        account.getAccountId()
                );

        // DB 저장
        account.updateRefreshToken(refreshToken);
        accountRepository.save(account);

        // HttpOnly Cookie 저장
        ResponseCookie cookie =
                ResponseCookie.from(
                                "refreshToken",
                                refreshToken
                        )
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(60 * 60 * 24 * 7)
                        .sameSite("Lax")
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
            );

        return Map.of(
                "success", true,
                "message", "로그인 성공",
                "accessToken", accessToken,
                "accountId", account.getAccountId(),
                "username", account.getUsername(),
                "status", account.getStatus(),
                "role", account.getRole()
        );
    }

    // 로그아웃
   @PostMapping("/logout")
    @ResponseBody
    public Map<String, Object> logout(
            HttpServletResponse response
    ) {

        ResponseCookie cookie =
                ResponseCookie.from("refreshToken", "")
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(0)
                        .sameSite("Lax")
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );

        return Map.of(
                "success", true,
                "message", "로그아웃 성공"
        );
    }

    // accessToken 재발급
    @PostMapping("/refresh")
    @ResponseBody
    public Map<String, Object> refresh(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Map.of(
                    "success", false,
                    "message", "쿠키가 없습니다."
            );
        }

        String refreshToken = null;

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            return Map.of(
                    "success", false,
                    "message", "refresh token이 없습니다."
            );
        }

        if (!jwtUtil.validateToken(refreshToken)) {

            return Map.of(
                    "success", false,
                    "message", "refresh 토큰 만료"
            );
        }

        String accountId =
                jwtUtil.getAccountId(refreshToken);

        Account account =
                accountRepository.findById(accountId)
                        .orElse(null);

        if (account == null ||
                !refreshToken.equals(
                        account.getRefreshToken()
                )) {

            return Map.of(
                    "success", false,
                    "message", "유효하지 않은 refresh token"
            );
        }

        String newAccessToken =
                jwtUtil.createAccessToken(
                        account.getAccountId(),
                        account.getUsername(),
                        account.getStatus(),
                        account.getRole()
                );

        return Map.of(
                "success", true,
                "accessToken", newAccessToken
        );
    }
}