package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.isPasswordMatch(password);
        return member;
    }
}
