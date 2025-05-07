package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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

        assertThatThrownBy(() -> loginService.login("test", "wrong_pw"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 맞지 않습니다.");

        assertThatThrownBy(() -> loginService.login("wrong_id", "test!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 맞지 않습니다.");

        assertThatThrownBy(() -> loginService.login("wrong_id", "wrong_pw"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 맞지 않습니다.");
    }
}