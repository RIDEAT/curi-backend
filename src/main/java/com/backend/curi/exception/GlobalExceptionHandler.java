package com.backend.curi.exception;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.ElementCollection;
import java.util.HashMap;
import java.util.Map;

import static com.backend.curi.exception.ErrorType.UNEXPECTED_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CuriException.class)
    public ResponseEntity<ExceptionResponse> handleCuriException(CuriException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionResponse>
    handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        MethodArgumentNotValidExceptionResponse errorResponse =
                MethodArgumentNotValidExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR);
        for (FieldError fieldError : e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponse(
                ErrorType.INVALID_DATA_DELETE.getErrorCode(),
                ErrorType.INVALID_DATA_DELETE.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        String unexpectedErrorTrace = ExceptionUtils.getStackTrace(e);
        log.error(unexpectedErrorTrace);
        return ResponseEntity.internalServerError()
                .body(
                        new ExceptionResponse(
                                UNEXPECTED_SERVER_ERROR.getErrorCode(),
                                UNEXPECTED_SERVER_ERROR.getMessage()));
    }
}
