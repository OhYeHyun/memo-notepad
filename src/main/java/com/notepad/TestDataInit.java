package com.notepad;

import com.notepad.user.service.UserService;
import com.notepad.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemoService memoService;
    private final UserService memberService;

//    @EventListener(ApplicationReadyEvent.class)
//    public void initData() {
//        String guestId = UUID.randomUUID().toString();
//        memoService.saveMemo("테스트 1", guestId);
//        memoService.saveMemo("테스트 2", guestId);
//
//        memberService.joinMember("테스터", "test", "test!");
//    }
}
