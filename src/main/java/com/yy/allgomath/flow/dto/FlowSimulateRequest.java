package com.yy.allgomath.flow.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FlowSimulateRequest(
        @NotNull double[][] particles,
        @DecimalMin("0.4") @DecimalMax("4.0") double scale,
        double time,
        @Min(1) @Max(200) int steps) {
}
