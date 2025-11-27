package com.notepad.core.exception;

import com.notepad.core.exception.custom.JwtException;
import com.notepad.core.exception.response.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleEntityNotFoundException(JwtException e) {
        return ResponseEntity
                .status(e.getExceptionReturnCode().returnCode())
                .body(new ExceptionResponse<>(
                                e.getExceptionReturnCode().getCode(),
                                e.getExceptionReturnCode().getMessage(),
                                null
                        )
                );
    }
}
