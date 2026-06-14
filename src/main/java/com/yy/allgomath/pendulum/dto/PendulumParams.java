package com.yy.allgomath.pendulum.dto;

/** 시뮬레이션 시작 상태 + 물리 파라미터. */
public record PendulumParams(PendulumState state, double gravity, double armRatio, double damping) {
}
