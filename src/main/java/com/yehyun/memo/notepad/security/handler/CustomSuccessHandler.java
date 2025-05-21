package com.yehyun.memo.notepad.security.handler;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import com.yehyun.memo.notepad.security.jwt.JwtRequestParser;
import com.yehyun.memo.notepad.service.MemoService;
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
    private final JwtRequestParser jwtRequestParser;
    private final MemoService memoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalMember principalMember = (PrincipalMember) authentication.getPrincipal();

        String guestToken = jwtRequestParser.extractTokenFromCookies(request.getCookies());

        if (guestToken != null) {
            Member guest = jwtRequestParser.createMemberFromToken(guestToken);
            memoService.updateWriterId(guest.getLoginId(), principalMember.getName());
        }

        jwtLoginSuccessProcessor.processSuccess(response, principalMember);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
