package com.yehyun.memo.notepad.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessProcessor {

    private final JwtProvider jwtProvider;

    public void processSuccess(HttpServletResponse response) {
        Cookie cookie = jwtProvider.expireCookie();
        response.addCookie(cookie);
    }
}
