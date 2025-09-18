package com.yehyun.memo.notepad.security.service;

import com.yehyun.memo.notepad.domain.user.User;
import com.yehyun.memo.notepad.repository.UserRepository;
import com.yehyun.memo.notepad.security.dto.PrincipalUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new PrincipalUser(user);
    }
}
