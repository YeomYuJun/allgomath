package com.yy.allgomath.automata.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 라이프 시뮬레이션 요청: 현재 그리드와 진행할 세대 수. */
public record LifeSimulateRequest(
        @NotNull boolean[][] grid,
        @Min(1) @Max(100) int steps) {
}
