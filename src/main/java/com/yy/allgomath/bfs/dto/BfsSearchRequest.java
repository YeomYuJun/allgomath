package com.yy.allgomath.bfs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BfsSearchRequest(
        @Min(4) @Max(40) int rows,
        @Min(4) @Max(60) int cols,
        @NotNull boolean[] walls,
        int start,
        int goal,
        boolean diag) {
}
