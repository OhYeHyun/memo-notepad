package com.yehyun.memo.notepad.domain.memo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoSaveForm {

    @NotBlank
    @Size(max = 30)
    private String content;
}
