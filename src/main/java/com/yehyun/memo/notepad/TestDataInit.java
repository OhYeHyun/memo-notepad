package com.yehyun.memo.notepad;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.repository.MemberRepository;
import com.yehyun.memo.notepad.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemoService memoService;
    private final MemberRepository memberRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        String guestId = UUID.randomUUID().toString();
        memoService.saveMemo("테스트 1", guestId);
        memoService.saveMemo("테스트 2", guestId);

        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("test!");
        member.setName("테스터");

        memberRepository.save(member);
    }
}
