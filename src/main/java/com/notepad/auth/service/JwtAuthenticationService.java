package com.notepad.auth.service;

import com.notepad.entity.User;
import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.global.enums.AuthStatus;
import com.notepad.global.enums.Role;
import com.notepad.global.enums.TokenName;
import com.notepad.auth.jwt.JwtLoginSuccessProcessor;
import com.notepad.auth.jwt.JwtProvider;
import com.notepad.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtProvider jwtProvider;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;
    private final RedisService redisService;
    private final UserService userService;

    public AuthStatus authenticateStatus(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.ACCESS_TOKEN);
        String refreshToken = jwtProvider.extractTokenFromCookies(request.getCookies(), TokenName.REFRESH_TOKEN);

        if (accessToken == null && refreshToken == null) {
            return AuthStatus.NO_TOKEN;
        }

        if (accessToken != null) {
            return handleAccessToken(accessToken, refreshToken, request, response);
        }

        return handleRefreshTokenOnly(refreshToken, request, response);
    }

    private AuthStatus handleAccessToken(String accessToken, String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (jwtProvider.isExpiredToken(accessToken)) {

            if (refreshToken != null && !jwtProvider.isExpiredToken(refreshToken)) {

                if (authenticateWithRefreshToken(request, response, refreshToken)) {
                    return AuthStatus.SUCCESS;
                }
                return AuthStatus.INVALID_TOKEN;
            }
            return AuthStatus.TOKEN_EXPIRED;
        }

        if (authenticateWithAccessToken(accessToken)) {
            return AuthStatus.SUCCESS;
        }
        return AuthStatus.INVALID_TOKEN;
    }

    private AuthStatus handleRefreshTokenOnly(String refreshToken,HttpServletRequest request, HttpServletResponse response) {
        if (jwtProvider.isExpiredToken(refreshToken)) {
            return AuthStatus.TOKEN_EXPIRED;
        }

        if (authenticateWithRefreshToken(request, response, refreshToken)) {
            return AuthStatus.SUCCESS;
        }
        return AuthStatus.INVALID_TOKEN;
    }

    private boolean authenticateWithAccessToken(String accessToken) {
        JwtPrincipal jwtPrincipal = jwtProvider.createMemberFromToken(accessToken);
        if (jwtPrincipal == null) {
            return false;
        }

        if (Role.ROLE_GUEST == jwtPrincipal.getRole()) {
            jwtLoginSuccessProcessor.applyAuthentication(jwtPrincipal);
            return true;
        }

        Optional<User> user = userService.findUser(jwtPrincipal.getId());
        if (user.isEmpty()) {
            return false;
        }

        jwtLoginSuccessProcessor.applyAuthentication(jwtPrincipal);
        return true;
    }

    private boolean authenticateWithRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        Long id = jwtProvider.getIdFromRefreshToken(refreshToken);
        if (id == null) {
            return false;
        }

        String savedRefreshToken = redisService.getRefreshTokenById(id);
        if (!refreshToken.equals(savedRefreshToken)) {
            return false;
        }

        Optional<User> user = userService.findUser(id);
        if (user.isEmpty()) {
            return false;
        }

        JwtPrincipal jwtPrincipal = new JwtPrincipal(user.get().getId(), user.get().getName(), user.get().getRole());
        jwtLoginSuccessProcessor.reissueAccessTokenAndAuthenticate(request, response, jwtPrincipal);
        return true;
    }
}
