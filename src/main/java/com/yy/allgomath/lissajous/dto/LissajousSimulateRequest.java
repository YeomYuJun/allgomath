package com.yy.allgomath.lissajous.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record LissajousSimulateRequest(
        @Min(1) @Max(9) int a,
        @Min(1) @Max(9) int b,
        @DecimalMin("0.0") @DecimalMax("6.2832") double delta,
        double phase,
        @Min(1) @Max(2000) int steps) {
}
