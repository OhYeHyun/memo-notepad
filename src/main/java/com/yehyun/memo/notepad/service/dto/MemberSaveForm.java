package com.yehyun.memo.notepad.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberSaveForm {

    @NotBlank
    private String name;

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
