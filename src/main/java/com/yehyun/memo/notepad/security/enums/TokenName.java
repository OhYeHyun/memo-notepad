package com.yehyun.memo.notepad.security.enums;

public enum TokenName {

    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token");

    private final String value;

    TokenName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
