package com.yy.allgomath.pendulum.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 진자 쌍의 현재 상태에서 다음 N step을 요청. */
public record PendulumSimulateRequest(
        @NotNull PendulumState state,
        @DecimalMin("0.3") @DecimalMax("2.6") double gravity,
        @DecimalMin("0.5") @DecimalMax("1.5") double armRatio,
        @DecimalMin("0.0") @DecimalMax("0.012") double damping,
        @Min(1) @Max(2000) int steps) {
}
