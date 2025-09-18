package com.yehyun.memo.notepad.security.handler;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.PrincipalUser;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();
        JwtPrincipal jwtPrincipal = jwtLoginSuccessProcessor.createJwtPrincipal(principalUser);

        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, jwtPrincipal);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
