package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.MemberDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtRequestParser {

    private final JwtUtil jwtUtil;

    public String createToken(String name, String loginId, String role) {
        return jwtUtil.createJwt(name, loginId, role);
    }

    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setMaxAge(216);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

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

    public MemberDto createMemberFromToken(String token) {
        try {
            String name = jwtUtil.getName(token);
            String loginId = jwtUtil.getLoginId(token);
            String role = jwtUtil.getRole(token);

            return new MemberDto(name, loginId, role);
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
}
