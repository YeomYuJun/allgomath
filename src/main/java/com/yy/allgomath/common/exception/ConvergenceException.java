package com.yy.allgomath.common.exception;

/** 수렴 실패 (HTTP 500). */
public class ConvergenceException extends ComputationException {
    public ConvergenceException(String message) { super(message); }
}
