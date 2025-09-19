package com.notepad.login.controller;

import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.auth.jwt.JwtLoginSuccessProcessor;
import com.notepad.auth.jwt.JwtLogoutSuccessProcessor;
import com.notepad.dto.request.user.UserLoginRequest;
import com.notepad.dto.response.user.UserClientResponse;
import com.notepad.login.service.GuestService;
import com.notepad.dto.request.user.UserSaveRequest;
import com.notepad.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.notepad.dto.response.user.UserClientResponse.ofUser;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final GuestService guestService;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;
    private final JwtLogoutSuccessProcessor jwtLogoutSuccessProcessor;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody UserSaveRequest saveRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        JwtPrincipal user = userService.createUser(saveRequest);
        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody UserLoginRequest infoRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        JwtPrincipal user = userService.login(infoRequest);
        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        jwtLogoutSuccessProcessor.processSuccess(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guest")
    public ResponseEntity<Void> guestLogin(HttpServletRequest request, HttpServletResponse response) {
        JwtPrincipal guest = guestService.createGuest();
        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, guest);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserClientResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ofUser(principal));
    }
}
