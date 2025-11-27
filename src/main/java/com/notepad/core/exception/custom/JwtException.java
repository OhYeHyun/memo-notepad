package com.notepad.core.exception.custom;

import com.notepad.global.enums.ExceptionReturnCode;
import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {
    private final ExceptionReturnCode exceptionReturnCode;

    public JwtException(ExceptionReturnCode exceptionReturnCode) {
        super(exceptionReturnCode.getMessage());
        this.exceptionReturnCode = exceptionReturnCode;
    }
}
