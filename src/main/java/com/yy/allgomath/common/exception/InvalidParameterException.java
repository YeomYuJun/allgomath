package com.yy.allgomath.common.exception;

/** 잘못된 입력 파라미터 (HTTP 400). */
public class InvalidParameterException extends AlgorithmException {
    public InvalidParameterException(String message) { super(message); }
    public InvalidParameterException(String message, Throwable cause) { super(message, cause); }
}
