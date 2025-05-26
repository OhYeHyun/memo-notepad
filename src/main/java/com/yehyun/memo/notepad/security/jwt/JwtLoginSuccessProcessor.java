package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.service.RedisService;
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
    private final RedisService redisService;

    public void reissueTokensAndAuthenticate(HttpServletRequest request, HttpServletResponse response, JwtPrincipal jwtPrincipal) {
        transferGuestDataIfExists(request, jwtPrincipal);

        String accessToken = jwtProvider.createAccessToken(
                jwtPrincipal.getName(),
                jwtPrincipal.getUsername(),
                jwtPrincipal.getRole()
        );
        String refreshToken = jwtProvider.createRefreshToken(jwtPrincipal.getUsername());

        addTokenToResponse(response, "access_token", accessToken);
        addTokenToResponse(response, "refresh_token", refreshToken);
        redisService.saveRefreshToken(jwtPrincipal.getUsername(), refreshToken, jwtProvider.getRefreshTokenExpiry());

        applyAuthentication(jwtPrincipal);
    }

    public void reissueAccessTokenAndAuthenticate(HttpServletResponse response, JwtPrincipal jwtPrincipal) {
        String accessToken = jwtProvider.createAccessToken(
                jwtPrincipal.getName(),
                jwtPrincipal.getUsername(),
                jwtPrincipal.getRole()
        );

        addTokenToResponse(response, "access_token", accessToken);

        applyAuthentication(jwtPrincipal);
    }

    private void transferGuestDataIfExists(HttpServletRequest request, JwtPrincipal jwtPrincipal) {
        String guestToken = jwtProvider.extractTokenFromCookies(request.getCookies(), "access_token");
        if (guestToken != null) {
            JwtPrincipal guest = jwtProvider.createMemberFromToken(guestToken);
            guestService.transferGuestMemos(guest, jwtPrincipal.getUsername());

            redisService.deleteRefreshToken(guest.getUsername());
        }
    }

    private void addTokenToResponse(HttpServletResponse response, String name, String token) {
        response.addCookie(jwtProvider.createCookie(name, token));
    }

    public void applyAuthentication(JwtPrincipal jwtPrincipal) {
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
