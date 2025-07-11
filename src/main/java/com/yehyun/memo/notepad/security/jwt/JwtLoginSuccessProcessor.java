package com.yehyun.memo.notepad.security.jwt;

import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.enums.Role;
import com.yehyun.memo.notepad.security.enums.TokenName;
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
                jwtPrincipal.getId(),
                jwtPrincipal.getName(),
                jwtPrincipal.getRole().name()
        );
        String refreshToken = jwtProvider.createRefreshToken(jwtPrincipal.getId());

        addTokenToResponse(response, TokenName.ACCESS_TOKEN, accessToken);
        addTokenToResponse(response, TokenName.REFRESH_TOKEN, refreshToken);
        redisService.saveRefreshToken(jwtPrincipal.getId(), refreshToken, jwtProvider.getRefreshTokenExpiry());

        applyAuthentication(jwtPrincipal);
    }

    public void reissueAccessTokenAndAuthenticate(HttpServletResponse response, JwtPrincipal jwtPrincipal) {
        String accessToken = jwtProvider.createAccessToken(
                jwtPrincipal.getId(),
                jwtPrincipal.getName(),
                jwtPrincipal.getRole().name()
        );

        addTokenToResponse(response, TokenName.ACCESS_TOKEN, accessToken);

        applyAuthentication(jwtPrincipal);
    }

    private void transferGuestDataIfExists(HttpServletRequest request, JwtPrincipal jwtPrincipal) {
        String guestToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.ACCESS_TOKEN);
        if (guestToken != null) {
            JwtPrincipal guest = jwtProvider.createMemberFromToken(guestToken);

            if (guest != null && guest.getRole() == Role.ROLE_GUEST) {
                guestService.transferGuestMemos(guest, jwtPrincipal.getId());
                redisService.deleteRefreshToken(guest.getId());
            }
        }
    }

    private void addTokenToResponse(HttpServletResponse response, TokenName name, String token) {
        response.addCookie(jwtProvider.createCookie(name, token));
    }

    public void applyAuthentication(JwtPrincipal jwtPrincipal) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtPrincipal, null, jwtPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public JwtPrincipal createJwtPrincipal(PrincipalMember principalMember) {
        return new JwtPrincipal(
                principalMember.getId(),
                principalMember.getName(),
                principalMember.getRole()
        );
    }
}
