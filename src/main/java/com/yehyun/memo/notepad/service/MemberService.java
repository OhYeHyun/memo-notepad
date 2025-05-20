package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.service.dto.MemberSaveForm;
import com.yehyun.memo.notepad.repository.MemberRepository;
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
        isDuplicated(memberSaveForm.getLoginId());

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

    private void isDuplicated(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
