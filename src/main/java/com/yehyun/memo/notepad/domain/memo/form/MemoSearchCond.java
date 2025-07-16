package com.yehyun.memo.notepad.domain.memo.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoSearchCond {

    private String content;
    private LocalDateTime createdDate;

    public MemoSearchCond() {
    }

    public MemoSearchCond(String content, LocalDateTime createdDate) {
        this.content = content;
        this.createdDate = createdDate;
    }
}
