package com.backend.curi.exception;


import com.backend.curi.common.Common;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static com.backend.curi.exception.ErrorType.UNEXPECTED_SERVER_ERROR;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final Common common;
    @ExceptionHandler(CuriException.class)
    public ResponseEntity<ExceptionResponse> handleCuriException(CuriException e) {
        log.warn("Current User Id: {} \nCuriException: {}", common.getCurrentUser().getUserId(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionResponse>
    handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Current User Id: {} \nMethod Argument Not Valid: {}", common.getCurrentUser().getUserId(), e.getMessage());
        MethodArgumentNotValidExceptionResponse errorResponse =
                MethodArgumentNotValidExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR);
        for (FieldError fieldError : e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.warn("Current User Id: {} \nData Integrity Not Valid: {}", common.getCurrentUser().getUserId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponse(
                ErrorType.INVALID_DATA_DELETE.getErrorCode(),
                ErrorType.INVALID_DATA_DELETE.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        String unexpectedErrorTrace = ExceptionUtils.getStackTrace(e);
        log.error("Current User Id: {} \nUnexpected ERROR: {}", common.getCurrentUser().getUserId(), e.getMessage());
        return ResponseEntity.internalServerError()
                .body(
                        new ExceptionResponse(
                                UNEXPECTED_SERVER_ERROR.getErrorCode(),
                                UNEXPECTED_SERVER_ERROR.getMessage()));
    }
}
