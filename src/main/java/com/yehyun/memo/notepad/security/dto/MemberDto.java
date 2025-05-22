package com.yehyun.memo.notepad.security.dto;

import lombok.Getter;

@Getter
public class MemberDto {

    private String name;
    private String loginId;
    private String role;

    public MemberDto(String name, String loginId, String role) {
        this.name = name;
        this.loginId = loginId;
        this.role = role;
    }

    public boolean isGuest() {
        return "ROLE_GUEST".equals(role);
    }
}
