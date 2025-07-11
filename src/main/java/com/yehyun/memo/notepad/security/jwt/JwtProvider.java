package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.Role;
import com.yehyun.memo.notepad.security.enums.TokenName;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtProvider {

    private static final Long COOKIE_MARGIN_EXPIRED_MS = Duration.ofMinutes(3).toMillis();

    private final JwtUtil jwtUtil;

    public String createAccessToken(Long id, String name, String role) {
        return jwtUtil.createAccessToken(id, name, role);
    }

    public String createRefreshToken(Long id) {
        return jwtUtil.createRefreshToken(id);
    }

    public JwtPrincipal createMemberFromToken(String token) {
        if (jwtUtil.isExpiredToken(token)) return null;

        Long id = Long.valueOf(jwtUtil.getId(token));
        String name = jwtUtil.getName(token);
        String roleString = jwtUtil.getRole(token);

        Role role = Role.valueOf(roleString);
        return new JwtPrincipal(id, name, role);
    }

    public Long getIdFromRefreshToken(String token) {
        if (jwtUtil.isExpiredToken(token)) return null;

        return Long.valueOf(jwtUtil.getId(token));
    }

    public Cookie createCookie(TokenName name, String token) {
        Cookie cookie = new Cookie(name.getValue(), token);
        cookie.setMaxAge(getCookieExpiry(name));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    private int getCookieExpiry(TokenName name) {
        if (TokenName.ACCESS_TOKEN.equals(name)) {
            return (int) (jwtUtil.getAccessTokenExpiry().getSeconds() +
                    Duration.ofMillis(COOKIE_MARGIN_EXPIRED_MS).getSeconds());
        }

        if (TokenName.REFRESH_TOKEN.equals(name)) {
            return (int) (jwtUtil.getRefreshTokenExpiry().getSeconds() +
                    Duration.ofMillis(COOKIE_MARGIN_EXPIRED_MS).getSeconds());
        }

        return 0;
    }

    public Cookie expireCookie(TokenName name) {
        Cookie cookie = new Cookie(name.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    public String extractTokenFromCookies(Cookie[] cookies, TokenName name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isExpiredToken(String token) {
        return jwtUtil.isExpiredToken(token);
    }

    public Duration getRefreshTokenExpiry() {
        return jwtUtil.getRefreshTokenExpiry();
    }
}
