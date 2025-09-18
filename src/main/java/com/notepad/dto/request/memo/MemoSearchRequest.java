package com.notepad.dto.request.memo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoSearchRequest {

    private String content;
    private LocalDateTime createdDate;

    public MemoSearchRequest() {
    }

    public MemoSearchRequest(String content, LocalDateTime createdDate) {
        this.content = content;
        this.createdDate = createdDate;
    }
}
