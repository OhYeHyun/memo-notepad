package com.notepad.global.enums;

import lombok.Getter;

@Getter
public enum ExceptionReturnCode {

    // 인증, 인가 관련
    TOKEN_UNAUTHORIZED("401", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED("440", "만료된 토큰입니다.");

    private final String code;
    private final String message;

    ExceptionReturnCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer returnCode() {
        return Integer.parseInt(code);
    }
}
