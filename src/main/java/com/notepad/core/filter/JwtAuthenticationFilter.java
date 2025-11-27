package com.notepad.core.filter;

import com.notepad.core.exception.custom.JwtException;
import com.notepad.global.enums.AuthStatus;
import com.notepad.auth.service.JwtAuthenticationService;
import com.notepad.global.enums.ExceptionReturnCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    private static final Set<String> PUBLIC_API_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/guest"
    );

    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String path = request.getRequestURI();

        if (!isApiPath(path) || PUBLIC_API_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthStatus status = jwtAuthenticationService.authenticateStatus(request, response);

        if (status == AuthStatus.SUCCESS) {
            request.setAttribute(REQUEST_ID, UUID.randomUUID().toString().substring(0, 8));
            Long userId = jwtAuthenticationService.authenticatedUserId();
            request.setAttribute(USER_ID, userId);

            filterChain.doFilter(request, response);
            return;
        }

        handleAuthenticationFailure(status);
    }

    private void handleAuthenticationFailure(AuthStatus status) {
        if (status == AuthStatus.TOKEN_EXPIRED) {
            throw new JwtException(ExceptionReturnCode.TOKEN_EXPIRED);
        }

        throw new JwtException(ExceptionReturnCode.TOKEN_UNAUTHORIZED);
    }

    private boolean isApiPath(String path) {
        return path.startsWith("/api/");
    }
}
