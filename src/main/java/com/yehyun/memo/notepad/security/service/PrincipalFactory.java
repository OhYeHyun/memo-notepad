package com.yehyun.memo.notepad.security.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import org.springframework.stereotype.Component;

@Component
public class PrincipalFactory {

    public PrincipalMember create(Member member) {
        return new PrincipalMember(member);
    }
}
