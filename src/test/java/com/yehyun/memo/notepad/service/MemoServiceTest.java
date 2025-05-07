package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.memo.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemoServiceTest {

    @Autowired
    MemoService memoService;

    @Test
    void saveMemo_success() {
        Memo savedMemo = memoService.saveMemo("111", 1L, null);
        Memo foundMemo = memoService.findById(savedMemo.getId());

        assertThat(savedMemo).isEqualTo(foundMemo);
    }

    @Test
    void savedMemo_null() {

    }

    @Test
    void toggleMemo() {
        Memo memo = memoService.saveMemo("111", 1L, null);
        memoService.toggleMemo(memo.getId());

        assertThat(memo.isChecked()).isTrue();
    }

    @Test
    void updateMemo() {
        Memo memo = memoService.saveMemo("111", 1L, null);
        memoService.updateMemo(memo.getId(), "222", true);

        assertThat(memo.getContent()).isEqualTo("222");
        assertThat(memo.isChecked()).isTrue();
    }

    @Test
    void deleteMemo() {
        Memo memo = memoService.saveMemo("111", 1L, null);
        memoService.deleteMemo(memo.getId());

        assertThatThrownBy(() -> memoService.findById(memo.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllMemos() {
        Memo memo1 = memoService.saveMemo("111", 1L, null);
        Memo memo2 = memoService.saveMemo("222", 2L, null);

        List<Memo> memos = memoService.getAllMemos();
        assertThat(memos).containsExactly(memo1, memo2);
    }
}