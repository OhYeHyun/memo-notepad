package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestLoginService {

    private final JwtUtil jwtUtil;

    public String createGuestToken() {
        String guestName = "guest";
        String guestId = UUID.randomUUID().toString();
        String guestRole = "ROLE_GUEST";

        return jwtUtil.createJwt(guestName, guestId, guestRole);
    }
}
