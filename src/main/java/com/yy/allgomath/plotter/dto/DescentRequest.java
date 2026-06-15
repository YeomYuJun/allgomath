package com.yy.allgomath.plotter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DescentRequest(
        @NotNull String fn,
        double startX,
        double startY,
        double learningRate,
        @Min(1) @Max(500) int maxIterations) {
}
