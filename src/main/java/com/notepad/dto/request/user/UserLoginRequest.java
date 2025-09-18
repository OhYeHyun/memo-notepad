package com.notepad.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank @Size(min = 5, max = 20) String loginId,
        @NotBlank @Size(min = 9, max = 30) String password
) {
}
