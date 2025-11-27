package com.notepad.user.service;

import com.notepad.auth.dto.PrincipalUser;
import com.notepad.auth.jwt.processor.JwtLoginSuccessProcessor;
import com.notepad.dto.request.user.UserLoginRequest;
import com.notepad.entity.User;
import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.global.enums.Role;
import com.notepad.dto.request.user.UserSaveRequest;
import com.notepad.user.repository.UserRepository;
import com.notepad.validator.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;

    @Transactional
    public JwtPrincipal createUser(UserSaveRequest request) {
        isDuplicatedLoginId(request.loginId());

        User guest = User.ofLocal(
                request.name(),
                request.loginId(),
                new BCryptPasswordEncoder().encode(request.password()),
                Role.ROLE_USER
        );
        userRepository.save(guest);

        return createMemberPrinciple(guest);
    }

    @Transactional
    public JwtPrincipal login(UserLoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        PrincipalUser principalUser = new PrincipalUser(user);
        return jwtLoginSuccessProcessor.createJwtPrincipal(principalUser);
    }

    private JwtPrincipal createMemberPrinciple(User user) {
        return new JwtPrincipal(user.getId(), user.getName(), user.getRole());
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public void isDuplicatedLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new ValidationException("loginId", "error.loginId.duplicated");
        }
    }
}
