package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member joinMember(String name, String loginId, String password) {
        isDuplicated(loginId);

        Member member = new Member(name, loginId, password);
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
