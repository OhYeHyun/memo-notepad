package com.yehyun.memo.notepad.domain.member.form;

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
