package com.yy.allgomath.common.exception;

/** 알고리즘 도메인 예외의 최상위 타입. */
public class AlgorithmException extends RuntimeException {
    public AlgorithmException(String message) { super(message); }
    public AlgorithmException(String message, Throwable cause) { super(message, cause); }
}
