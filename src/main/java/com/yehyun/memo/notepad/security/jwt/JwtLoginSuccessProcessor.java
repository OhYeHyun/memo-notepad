package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.service.GuestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessProcessor {

    private final JwtProvider jwtProvider;
    private final GuestService guestService;

    public void processSuccess(HttpServletRequest request, HttpServletResponse response, JwtPrincipal jwtPrincipal) {

        String guestToken = jwtProvider.extractTokenFromCookies(request.getCookies());
        if (guestToken != null) {
            JwtPrincipal guest = jwtProvider.createMemberFromToken(guestToken);
            guestService.transferGuestMemos(guest, jwtPrincipal.getUsername());
        }

        addToSecurityContextHolder(jwtPrincipal);
        String token = jwtProvider.createToken(
                jwtPrincipal.getName(),
                jwtPrincipal.getUsername(),
                jwtPrincipal.getRole()
        );

        response.addCookie(jwtProvider.createCookie(token));
    }

    public void addToSecurityContextHolder(JwtPrincipal jwtPrincipal) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtPrincipal, null, jwtPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public JwtPrincipal createJwtPrincipal(PrincipalMember principalMember) {
        return new JwtPrincipal(
                principalMember.getNickname(),
                principalMember.getUsername(),
                principalMember.getRole()
        );
    }
}
