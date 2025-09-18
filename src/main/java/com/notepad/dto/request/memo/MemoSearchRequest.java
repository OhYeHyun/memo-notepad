package com.notepad.dto.request.memo;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record MemoSearchRequest(
        String content,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime createdAt
) {
}
