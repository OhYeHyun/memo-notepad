package com.notepad.dto.response.user;

import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.global.enums.Role;
import lombok.Builder;

@Builder
public record UserClientResponse(
        String name,
        Role role
) {
    public static UserClientResponse ofUser(JwtPrincipal principal) {
        return UserClientResponse.builder()
                .name(principal.getName())
                .role(principal.getRole())
                .build();
    }
}
