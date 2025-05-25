package com.yehyun.memo.notepad.security.handler;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import com.yehyun.memo.notepad.security.jwt.JwtProvider;
import com.yehyun.memo.notepad.service.GuestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;
    private final JwtProvider jwtProvider;
    private final GuestService guestService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalMember principalMember = (PrincipalMember) authentication.getPrincipal();
        JwtPrincipal jwtPrincipal = new JwtPrincipal(
                principalMember.getNickname(),
                principalMember.getUsername(),
                principalMember.getRole()
        );

        String guestToken = jwtProvider.extractTokenFromCookies(request.getCookies());
        if (guestToken != null) {
            JwtPrincipal guest = jwtProvider.createMemberFromToken(guestToken);
            guestService.transferGuestMemos(guest, principalMember.getName());
        }

        jwtLoginSuccessProcessor.processSuccess(response, jwtPrincipal);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
