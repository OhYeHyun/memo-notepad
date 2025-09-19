package com.notepad.auth.jwt;

import com.notepad.global.enums.AuthStatus;
import com.notepad.auth.service.JwtAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthStatus status = jwtAuthenticationService.authenticateStatus(request, response);
        if (status == AuthStatus.SUCCESS) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isApiPath(path)) {
            response.setStatus(status == AuthStatus.TOKEN_EXPIRED ? 440 : HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.equals("/app") ||
                path.startsWith("/app/") ||
                path.startsWith("/assets") ||
                path.startsWith("/css/") ||
                path.startsWith("/image") ||
                path.startsWith("/actuator") ||
                (path.startsWith("/api/auth") && !path.contains("/me")) ||
                path.startsWith("/oauth2") ||
                path.startsWith("/login/oauth2") ||
                path.equals("/error") ||
                path.equals("/favicon.ico");
    }

    private boolean isApiPath(String path) {
        return path.startsWith("/api/");
    }
}
