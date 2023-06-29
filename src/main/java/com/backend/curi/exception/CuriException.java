package com.backend.curi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CuriException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final ExceptionResponse body;

    public CuriException(HttpStatus httpStatus, ErrorType errorType) {
        super(errorType.getMessage());
        this.httpStatus = httpStatus;
        this.body = new ExceptionResponse(errorType.getErrorCode(), errorType.getMessage());
    }
}
