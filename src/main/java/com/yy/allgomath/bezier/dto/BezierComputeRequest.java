package com.yy.allgomath.bezier.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BezierComputeRequest(
        @NotNull double[][] controlPoints,
        @Min(2) @Max(400) int samples) {
}
