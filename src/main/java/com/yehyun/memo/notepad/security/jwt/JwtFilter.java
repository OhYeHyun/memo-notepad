package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/login") || path.startsWith("/login/signup") || path.startsWith("/css/**")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromCookies(request.getCookies());
        if (token == null) {
            log.info("token null");
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(token)) {
            log.info("token expired");
            response.sendRedirect("/login?error=expired");
            return;
        }

        Member member = createMemberFromToken(token);
        PrincipalMember principalMember = new PrincipalMember(member);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principalMember, null, principalMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Member createMemberFromToken(String token) {
        return new Member(
                jwtUtil.getName(token),
                jwtUtil.getLoginId(token),
                jwtUtil.getRole(token)
        );
    }
}
