package com.notepad.dto.response.memo;

import com.notepad.entity.Memo;
import lombok.Builder;

import java.util.List;

@Builder
public record MemoClientResponse(
        Long memoId,
        String content,
        Boolean isChecked
) {
    public static List<MemoClientResponse> ofMemos(List<Memo> memoList) {
        return memoList.stream()
                .map(MemoClientResponse::ofMemo)
                .toList();
    }

    public static MemoClientResponse ofMemo(Memo memo) {
        return MemoClientResponse.builder()
                .memoId(memo.getId())
                .content(memo.getContent())
                .isChecked(memo.getIsChecked())
                .build();
    }
}
