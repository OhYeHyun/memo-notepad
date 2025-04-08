package com.yehyun.memo.notepad;

import com.yehyun.memo.notepad.domain.Memo;
import com.yehyun.memo.notepad.domain.form.MemoSaveForm;
import com.yehyun.memo.notepad.repository.MemoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemoRepository memoRepository;

    @PostConstruct
    public void init() {
        memoRepository.save(new Memo("테스트 1"));
        memoRepository.save(new Memo("테스트 2"));
    }
}
