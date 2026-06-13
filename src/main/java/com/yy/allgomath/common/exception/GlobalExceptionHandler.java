package com.yy.allgomath.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 전역 예외 처리. 모든 에러 응답을 {@link ErrorResponse} 형태로 통일한다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(ComputationException.class)
    public ResponseEntity<ErrorResponse> handleComputation(ComputationException ex, WebRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Computation Error", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "서버 내부 오류가 발생했습니다.", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message,
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
