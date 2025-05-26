package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessProcessor {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    public void processSuccess(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.extractTokenFromCookies(request.getCookies(), "access_token");
        JwtPrincipal jwtPrincipal = jwtProvider.createMemberFromToken(accessToken);
        redisService.deleteRefreshToken(jwtPrincipal.getUsername());

        Cookie accessCookie = jwtProvider.expireCookie("access_token");
        response.addCookie(accessCookie);

        Cookie refreshCookie = jwtProvider.expireCookie("refresh_token");
        response.addCookie(refreshCookie);
    }
}
