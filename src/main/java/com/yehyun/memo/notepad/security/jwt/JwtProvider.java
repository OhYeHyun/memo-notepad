package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtProvider {

    private final JwtUtil jwtUtil;

    public String createToken(String name, String loginId, String role) {
        return jwtUtil.createJwt(name, loginId, role);
    }

    public JwtPrincipal createMemberFromToken(String token) {
        if (jwtUtil.isExpired(token)) return null;

        String name = jwtUtil.getName(token);
        String loginId = jwtUtil.getLoginId(token);
        String role = jwtUtil.getRole(token);

        return new JwtPrincipal(name, loginId, role);
    }

    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setMaxAge(216);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    public Cookie expireCookie() {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    public String extractTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
