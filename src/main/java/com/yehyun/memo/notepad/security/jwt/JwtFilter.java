package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtRequestParser jwtRequestParser;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/login") || path.startsWith("/login/signup") || path.startsWith("/oauth2") || path.startsWith("/css/") || path.startsWith("/image")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtRequestParser.extractTokenFromCookies(request.getCookies());
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

        Member member = jwtRequestParser.createMemberFromToken(token);
        PrincipalMember principalMember = new PrincipalMember(member);
        jwtLoginSuccessProcessor.addToSecurityContextHolder(principalMember);

        filterChain.doFilter(request, response);
    }
}
