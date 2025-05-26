package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final MemoService memoService;

    public JwtPrincipal createGuestMember() {
        Member guestMember = Member.ofLocal(
                "guest",
                "guest_" + UUID.randomUUID(),
                "",
                Role.ROLE_GUEST
        );

        return createGuestPrincipal(guestMember);
    }

    private JwtPrincipal createGuestPrincipal(Member guest) {
        return new JwtPrincipal(guest.getName(), guest.getLoginId(), guest.getRole());
    }

    public void transferGuestMemos(JwtPrincipal existingMember, String writerId) {
        if (isGuest(existingMember)) {
            memoService.updateWriterId(existingMember.getUsername(), writerId);
        }
    }

    private boolean isGuest(JwtPrincipal member) {
        return member != null && Role.ROLE_GUEST == member.getRole();
    }
}
