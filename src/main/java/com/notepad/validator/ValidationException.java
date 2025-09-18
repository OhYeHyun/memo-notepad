package com.notepad.validator;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String field;
    private final String errorCode;

    public ValidationException(String field, String errorCode) {
        this.field = field;
        this.errorCode = errorCode;
    }
}
