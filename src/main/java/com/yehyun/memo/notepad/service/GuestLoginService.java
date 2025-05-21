package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.service.PrincipalFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestLoginService {

    private final PrincipalFactory principalFactory;

    public PrincipalMember createGuestPrincipal() {
        String guestId = UUID.randomUUID().toString();
        Member guestMember = new Member("guest", guestId, "ROLE_GUEST");
        return principalFactory.create(guestMember);
    }
}
