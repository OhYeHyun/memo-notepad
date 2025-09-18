package com.notepad.dto.request.memo;

import jakarta.validation.constraints.Size;

public record MemoCreateRequest(
        @Size(min = 1, max = 30) String content
) {
}
