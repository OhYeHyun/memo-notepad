package com.yehyun.memo.notepad.domain.memo;

import lombok.Data;

@Data
public class Memo {

    private Long id;
    private String content;
    private boolean check;
    private String writerId;

    public Memo(String content, String writerId) {
        this.content = content;
        this.check = false;
        this.writerId = writerId;
    }

    public void update(String content) {
        this.content = content;
    }

    public void toggleCheck() {
        this.check = !this.check;
    }
}
