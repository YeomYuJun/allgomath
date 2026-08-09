package com.yy.allgomath.voronoi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoronoiComputeRequest(
        @NotNull @Size(max = 64) double[][] sites,
        @NotNull String metric,
        @Min(16) @Max(200) int grid) {
}
