package com.yehyun.memo.notepad.security.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import com.yehyun.memo.notepad.security.jwt.JwtProvider;
import com.yehyun.memo.notepad.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtProvider jwtProvider;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;
    private final RedisService redisService;
    private final MemberService memberService;

    public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.extractTokenFromCookies(request.getCookies(), "access_token");
        if (isValidToken(accessToken)) {
            return authenticateWithAccessToken(accessToken);
        }

        String refreshToken = jwtProvider.extractTokenFromCookies(request.getCookies(), "refresh_token");
        if (isValidToken(refreshToken)) {
            return authenticateWithRefreshToken(request, response, refreshToken);
        }

        return false;
    }

    private boolean isValidToken(String token) {
        return token != null && !jwtProvider.isExpiredToken(token);
    }

    private boolean authenticateWithAccessToken(String accessToken) {
        JwtPrincipal jwtPrincipal = jwtProvider.createMemberFromToken(accessToken);
        if (jwtPrincipal == null) return false;

        Member member = memberService.findByLoginId(jwtPrincipal.getUsername()).orElse(null);
        if (member == null && !"ROLE_GUEST".equals(jwtPrincipal.getRole())) return false;

        jwtLoginSuccessProcessor.applyAuthentication(jwtPrincipal);
        return true;
    }

    private boolean authenticateWithRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        String loginId = jwtProvider.getLoginIdFromRefreshToken(refreshToken);
        if (loginId == null) return false;

        String savedRefreshToken = redisService.getRefreshTokenByLoginId(loginId);
        if (!refreshToken.equals(savedRefreshToken)) return false;

        if (loginId.startsWith("guest_")) {
            JwtPrincipal jwtPrincipal = new JwtPrincipal("guest", loginId, "ROLE_GUEST");
            jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, jwtPrincipal);
            return true;
        }

        Member member = memberService.findByLoginId(loginId).orElse(null);
        if (member == null) return false;

        JwtPrincipal jwtPrincipal = new JwtPrincipal(member.getName(), member.getLoginId(), member.getRole());
        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, jwtPrincipal);
        return true;
    }
}
