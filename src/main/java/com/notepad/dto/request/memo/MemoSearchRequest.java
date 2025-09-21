package com.notepad.dto.request.memo;

import com.notepad.global.enums.SearchMode;
import com.notepad.global.enums.SortBy;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MemoSearchRequest(
        String q,
        SearchMode mode,
        SortBy sortBy,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
) {
}
