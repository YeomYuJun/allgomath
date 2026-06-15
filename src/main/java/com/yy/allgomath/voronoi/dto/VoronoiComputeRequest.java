package com.yy.allgomath.voronoi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VoronoiComputeRequest(
        @NotNull double[][] sites,
        @NotNull String metric,
        @Min(16) @Max(200) int grid) {
}
