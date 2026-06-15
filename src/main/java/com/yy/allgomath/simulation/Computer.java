package com.yy.allgomath.simulation;

/** 비반복 one-shot 연산 계약(BatchSimulator의 비반복 형제). */
public interface Computer<P, R> {
    R compute(P params);
}
