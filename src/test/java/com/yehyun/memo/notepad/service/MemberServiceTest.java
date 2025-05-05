package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void joinMember_success() {
        Member savedMember = memberService.joinMember("테스터", "test", "test!");
        Member foundMember = memberService.findById(savedMember.getId());

        assertThat(savedMember).isEqualTo(foundMember);
    }

    @Test
    void joinMember_duplicatedId() {
        memberService.joinMember("테스터1", "test", "test!");

        assertThatThrownBy(() -> memberService.joinMember("테스터2", "test", "test!"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}