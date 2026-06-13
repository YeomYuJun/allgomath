package com.yy.allgomath.common.exception;

/** 계산 중 실패 (HTTP 500). */
public class ComputationException extends AlgorithmException {
    public ComputationException(String message) { super(message); }
    public ComputationException(String message, Throwable cause) { super(message, cause); }
}
