package com.yy.allgomath.common.exception;

/** 파라미터 범위 위반 (HTTP 400). */
public class ParameterRangeException extends InvalidParameterException {
    public ParameterRangeException(String message) { super(message); }
    public ParameterRangeException(String message, Throwable cause) { super(message, cause); }
}
