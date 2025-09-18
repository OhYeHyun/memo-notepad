package com.notepad.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSaveRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 5, max = 20)
    private String loginId;

    @NotBlank
    @Size(min = 9, max = 30)
    private String password;
}
