package com.yy.allgomath.plotter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlotterSurfaceRequest(
        @NotNull String fn,
        double range,
        @Min(8) @Max(80) int resolution) {
}
