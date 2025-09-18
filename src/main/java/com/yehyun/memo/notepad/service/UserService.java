package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.user.User;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.enums.Role;
import com.yehyun.memo.notepad.service.dto.UserSaveForm;
import com.yehyun.memo.notepad.repository.UserRepository;
import com.yehyun.memo.notepad.validator.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public JwtPrincipal createUser(UserSaveForm form) {
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

    private void validateUserSaveForm(UserSaveForm form) {
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
