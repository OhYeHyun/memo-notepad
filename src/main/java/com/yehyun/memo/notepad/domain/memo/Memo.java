package com.yehyun.memo.notepad.domain.memo;

import lombok.Data;

@Data
public class Memo {

    private Long id;
    private String content;
    private boolean check;

    public Memo(String content) {
        this.content = content;
    }
}
