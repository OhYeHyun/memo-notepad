package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessProcessor {

    private final JwtRequestParser jwtRequestParser;

    public void processSuccess(HttpServletResponse response, PrincipalMember principalMember) {
        addToSecurityContextHolder(principalMember);

        String token = jwtRequestParser.createToken(
                principalMember.getNickname(),
                principalMember.getUsername(),
                principalMember.getRole()
        );

        response.addCookie(jwtRequestParser.createCookie(token));
    }

    public void addToSecurityContextHolder(PrincipalMember principalMember) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principalMember, null, principalMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
