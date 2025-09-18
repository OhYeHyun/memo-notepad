package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.user.User;
import com.yehyun.memo.notepad.repository.UserRepository;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final MemoService memoService;
    private final UserRepository userRepository;

    @Transactional
    public JwtPrincipal createGuest() {
        User guest = User.ofLocal(
                "guest",
                "guest_" + UUID.randomUUID(),
                "",
                Role.ROLE_GUEST
        );
        userRepository.save(guest);

        return createGuestPrincipal(guest);
    }

    private JwtPrincipal createGuestPrincipal(User guest) {
        return new JwtPrincipal(guest.getId(), guest.getName(), guest.getRole());
    }

    public void transferGuestMemos(JwtPrincipal existingUser, Long writerId) {
        if (isGuest(existingUser)) {
            memoService.updateWriter(existingUser.getId(), writerId);
        }
    }

    private boolean isGuest(JwtPrincipal user) {
        return user != null && Role.ROLE_GUEST == user.getRole();
    }
}


