package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.repository.MemberRepository;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final MemoService memoService;
    private final MemberRepository memberRepository;

    public JwtPrincipal createGuestMember() {
        Member guestMember = Member.ofLocal(
                "guest",
                "guest_" + UUID.randomUUID(),
                "",
                Role.ROLE_GUEST
        );
        memberRepository.save(guestMember);

        return createGuestPrincipal(guestMember);
    }

    private JwtPrincipal createGuestPrincipal(Member guest) {
        return new JwtPrincipal(guest.getId(), guest.getName(), guest.getRole());
    }

    public void transferGuestMemos(JwtPrincipal existingMember, Long writerId) {
        if (isGuest(existingMember)) {
            memoService.updateWriterId(existingMember.getId(), writerId);
        }
    }

    private boolean isGuest(JwtPrincipal member) {
        return member != null && Role.ROLE_GUEST == member.getRole();
    }
}


