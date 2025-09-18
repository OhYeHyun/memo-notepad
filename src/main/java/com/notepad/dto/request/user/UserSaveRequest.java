package com.notepad.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSaveRequest(
        @NotBlank @Size(min = 1, max = 8) String name,
        @NotBlank @Pattern(regexp = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9]{5,20}$") String loginId,
        @NotBlank @Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>]).{9,30}$") String password
) {
}
