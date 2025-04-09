package com.yehyun.memo.notepad;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.repository.MemberRepository;
import com.yehyun.memo.notepad.repository.MemoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        String guestId = UUID.randomUUID().toString();
        memoRepository.save(new Memo("테스트 1", guestId));
        memoRepository.save(new Memo("테스트 2", guestId));

        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("test!");
        member.setName("테스터");

        memberRepository.save(member);
    }
}
