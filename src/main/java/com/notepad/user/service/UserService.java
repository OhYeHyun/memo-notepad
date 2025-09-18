package com.notepad.user.service;

import com.notepad.entity.User;
import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.global.enums.Role;
import com.notepad.dto.request.user.UserSaveRequest;
import com.notepad.user.repository.UserRepository;
import com.notepad.validator.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public JwtPrincipal createUser(UserSaveRequest form) {
        validateUserSaveForm(form);

        User guest = User.ofLocal(
                form.getName(),
                form.getLoginId(),
                new BCryptPasswordEncoder().encode(form.getPassword()),
                Role.ROLE_USER
        );
        userRepository.save(guest);

        return createMemberPrinciple(guest);
    }

    private JwtPrincipal createMemberPrinciple(User user) {
        return new JwtPrincipal(user.getId(), user.getName(), user.getRole());
    }

    @Transactional(readOnly = true)
    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    private void validateUserSaveForm(UserSaveRequest form) {
        isDuplicatedLoginId(form.getLoginId());
        validateFormatLoginId(form.getLoginId());
        validateFormatPassword(form.getPassword());
    }

    private void isDuplicatedLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new ValidationException("loginId", "error.loginId.duplicated");
        }
    }

    private void validateFormatLoginId(String loginId) {
        if (!loginId.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("loginId", "error.loginId.format");
        }
    }

    private void validateFormatPassword(String password) {
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new ValidationException("password", "error.password.format");
        }
    }
}
