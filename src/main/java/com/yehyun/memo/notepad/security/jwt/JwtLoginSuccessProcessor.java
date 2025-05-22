package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessProcessor {

    private final JwtRequestParser jwtRequestParser;

    public void processSuccess(HttpServletResponse response, JwtPrincipal jwtPrincipal) {
        addToSecurityContextHolder(jwtPrincipal);

        String token = jwtRequestParser.createToken(
                jwtPrincipal.getName(),
                jwtPrincipal.getUsername(),
                jwtPrincipal.getRole()
        );

        response.addCookie(jwtRequestParser.createCookie(token));
    }

    public void addToSecurityContextHolder(JwtPrincipal jwtPrincipal) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtPrincipal, null, jwtPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
