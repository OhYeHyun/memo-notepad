package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.TokenName;
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
        String accessToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.ACCESS_TOKEN);
        JwtPrincipal jwtPrincipal = jwtProvider.createMemberFromToken(accessToken);

        redisService.deleteRefreshToken(jwtPrincipal.getId());

        Cookie accessCookie = jwtProvider.expireCookie(TokenName.ACCESS_TOKEN);
        response.addCookie(accessCookie);

        Cookie refreshCookie = jwtProvider.expireCookie(TokenName.REFRESH_TOKEN);
        response.addCookie(refreshCookie);
    }
}
