package com.notepad.dto.request.memo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemoUpdateRequest {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String content;
}
