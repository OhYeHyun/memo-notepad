package com.yehyun.memo.notepad.security.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.AuthStatus;
import com.yehyun.memo.notepad.security.enums.Role;
import com.yehyun.memo.notepad.security.enums.TokenName;
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

    public AuthStatus authenticateStatus(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.ACCESS_TOKEN);
        String refreshToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.REFRESH_TOKEN);

        if (accessToken == null && refreshToken == null) {
            return AuthStatus.NO_TOKEN;
        }

        if (accessToken != null) {
            return handleAccessToken(accessToken, refreshToken, response);
        }

        return handleRefreshTokenOnly(refreshToken, response);
    }

    private AuthStatus handleAccessToken(String accessToken, String refreshToken, HttpServletResponse response) {
        if (jwtProvider.isExpiredToken(accessToken)) {

            if (refreshToken != null && !jwtProvider.isExpiredToken(refreshToken)) {

                if (authenticateWithRefreshToken(response, refreshToken)) {
                    return AuthStatus.SUCCESS;
                }
                return AuthStatus.INVALID_TOKEN;
            }
            return AuthStatus.TOKEN_EXPIRED;
        }

        if (authenticateWithAccessToken(accessToken)) {
            return AuthStatus.SUCCESS;
        }
        return AuthStatus.INVALID_TOKEN;
    }

    private AuthStatus handleRefreshTokenOnly(String refreshToken, HttpServletResponse response) {
        if (jwtProvider.isExpiredToken(refreshToken)) {
            return AuthStatus.TOKEN_EXPIRED;
        }

        if (authenticateWithRefreshToken(response, refreshToken)) {
            return AuthStatus.SUCCESS;
        }
        return AuthStatus.INVALID_TOKEN;
    }

    private boolean authenticateWithAccessToken(String accessToken) {
        JwtPrincipal jwtPrincipal = jwtProvider.createMemberFromToken(accessToken);
        if (jwtPrincipal == null) {
            return false;
        }

        if (Role.ROLE_GUEST == jwtPrincipal.getRole()) {
            jwtLoginSuccessProcessor.applyAuthentication(jwtPrincipal);
            return true;
        }

        Member member = memberService.findByLoginId(jwtPrincipal.getUsername()).orElse(null);
        if (member == null) {
            return false;
        }

        jwtLoginSuccessProcessor.applyAuthentication(jwtPrincipal);
        return true;
    }

    private boolean authenticateWithRefreshToken(HttpServletResponse response, String refreshToken) {
        String loginId = jwtProvider.getLoginIdFromRefreshToken(refreshToken);
        if (loginId == null) {
            return false;
        }

        String savedRefreshToken = redisService.getRefreshTokenByLoginId(loginId);
        if (!refreshToken.equals(savedRefreshToken)) {
            return false;
        }

        if (loginId.startsWith("guest_")) {
            JwtPrincipal jwtPrincipal = new JwtPrincipal("guest", loginId, Role.ROLE_GUEST);
            jwtLoginSuccessProcessor.reissueAccessTokenAndAuthenticate(response, jwtPrincipal);
            return true;
        }

        Member member = memberService.findByLoginId(loginId).orElse(null);
        if (member == null) {
            return false;
        }

        JwtPrincipal jwtPrincipal = new JwtPrincipal(member.getName(), member.getLoginId(), member.getRole());
        jwtLoginSuccessProcessor.reissueAccessTokenAndAuthenticate(response, jwtPrincipal);
        return true;
    }
}
