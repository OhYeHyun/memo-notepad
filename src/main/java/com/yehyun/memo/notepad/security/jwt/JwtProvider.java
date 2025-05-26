package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtProvider {

    private final JwtUtil jwtUtil;

    public String createAccessToken(String name, String loginId, String role) {
        return jwtUtil.createAccessToken(name, loginId, role);
    }

    public String createRefreshToken(String name) {
        return jwtUtil.createRefreshToken(name);
    }

    public JwtPrincipal createMemberFromToken(String token) {
        if (jwtUtil.isExpiredToken(token)) return null;

        String name = jwtUtil.getName(token);
        String loginId = jwtUtil.getLoginId(token);
        String role = jwtUtil.getRole(token);

        return new JwtPrincipal(name, loginId, role);
    }

    public String getLoginIdFromRefreshToken(String token) {
        if (jwtUtil.isExpiredToken(token)) return null;

        return jwtUtil.getLoginId(token);
    }

    public Cookie createCookie(String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setMaxAge(getTokenExpiry(name));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    private int getTokenExpiry(String name) {
        if ("access_token".equals(name)) {
            return (int) jwtUtil.getAccessTokenExpiry().getSeconds();
        }

        if ("refresh_token".equals(name)) {
            return (int) jwtUtil.getRefreshTokenExpiry().getSeconds();
        }

        return 0;
    }

    public Cookie expireCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    public String extractTokenFromCookies(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isExpiredAccessToken(String token) {
        return jwtUtil.isExpiredToken(token);
    }

    public Duration getRefreshTokenExpiry() {
        return jwtUtil.getRefreshTokenExpiry();
    }
}
