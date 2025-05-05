package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LoginServiceTest {

    @Autowired
    LoginService loginService;
    @Autowired
    MemberService memberService;

    @Test
    void login_success() {
        Member joinMember = memberService.joinMember("테스터", "test", "test!");
        Member loginMember = loginService.login("test", "test!");

        assertThat(joinMember).isEqualTo(loginMember);
    }

    @Test
    void login_fail() {
        memberService.joinMember("테스터", "test", "test!");

        Member case1 = loginService.login("test", "wrong_pw");
        Member case2 = loginService.login("wrong_id", "test!");
        Member case3 = loginService.login("wrong_id", "wrong_pw");

        assertThat(case1).isNull();
        assertThat(case2).isNull();
        assertThat(case3).isNull();
    }
}