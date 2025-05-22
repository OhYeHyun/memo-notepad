package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.JwtPrincipal;
import com.yehyun.memo.notepad.security.dto.MemberDto;
import com.yehyun.memo.notepad.security.jwt.JwtRequestParser;
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
    private final GuestService guestService;
    private final JwtRequestParser jwtRequestParser;

    public JwtPrincipal signupWithPossibleGuest(MemberSaveForm form, String existingToken) {
        validateMemberSaveForm(form);

        if (existingToken != null) {
            MemberDto existingMember = jwtRequestParser.createMemberFromToken(existingToken);
            guestService.transferGuestMemos(existingMember, form.getLoginId());
        }

        return createGuestMember(form);
    }

    private JwtPrincipal createGuestMember(MemberSaveForm form) {
        Member member = Member.ofLocal(
                form.getName(),
                form.getLoginId(),
                bCryptPasswordEncoder.encode(form.getPassword()),
                "ROLE_USER"
        );
        memberRepository.save(member);

        return createMemberPrinciple(member);
    }

    private JwtPrincipal createMemberPrinciple(Member member) {
        return new JwtPrincipal(member.getName(), member.getLoginId(), member.getRole());
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
