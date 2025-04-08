package com.yehyun.memo.notepad.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String content;

    private boolean check;
}
