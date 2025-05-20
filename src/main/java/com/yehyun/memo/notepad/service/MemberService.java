package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.service.dto.MemberSaveForm;
import com.yehyun.memo.notepad.repository.MemberRepository;
import com.yehyun.memo.notepad.validator.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Member joinMember(MemberSaveForm memberSaveForm) {
        validateMemberSaveForm(memberSaveForm);

        Member member = new Member(
                memberSaveForm.getName(),
                memberSaveForm.getLoginId(),
                bCryptPasswordEncoder.encode(memberSaveForm.getPassword())
        );

        return memberRepository.save(member);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow();
    }

    private void validateMemberSaveForm(MemberSaveForm form) {
        isDuplicatedLoginId(form.getLoginId());
        validateFormatLoginId(form.getLoginId());
        validateFormatPassword(form.getPassword());
    }

    private void isDuplicatedLoginId(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
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
