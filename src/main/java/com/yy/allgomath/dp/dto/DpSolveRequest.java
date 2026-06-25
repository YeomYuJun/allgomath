package com.yy.allgomath.dp.dto;

import jakarta.validation.constraints.NotNull;

public record DpSolveRequest(
        @NotNull int[][] grid,
        @NotNull String mode) {
}
