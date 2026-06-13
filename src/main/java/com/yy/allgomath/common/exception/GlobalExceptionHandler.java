package com.yy.allgomath.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 전역 예외 처리. 도메인 예외를 {@link ErrorResponse} 형태로 통일한다.
 * <p>
 * {@link ResponseEntityExceptionHandler}를 상속하여 Spring MVC 프레임워크 예외
 * (404/405/415, 바인딩/검증 등)는 프레임워크가 올바른 4xx로 처리하도록 위임한다.
 * 따라서 {@code MethodArgumentNotValidException}/{@code MissingServletRequestParameterException}는
 * 여기서 직접 매핑하지 않는다(부모와의 중복 매핑 회피).
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(ComputationException.class)
    public ResponseEntity<ErrorResponse> handleComputation(ComputationException ex, WebRequest request) {
        log.error("계산 오류", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Computation Error", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        log.error("처리되지 않은 예외", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "서버 내부 오류가 발생했습니다.", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message,
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
