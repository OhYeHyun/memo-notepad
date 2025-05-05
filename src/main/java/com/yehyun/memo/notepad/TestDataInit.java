package com.yehyun.memo.notepad;

import com.yehyun.memo.notepad.service.MemberService;
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
    private final MemberService memberService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        String guestId = UUID.randomUUID().toString();
        memoService.saveMemo("테스트 1", guestId);
        memoService.saveMemo("테스트 2", guestId);

        memberService.joinMember("테스터", "test", "test!");
    }
}
