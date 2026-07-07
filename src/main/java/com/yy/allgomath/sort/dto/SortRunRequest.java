package com.yy.allgomath.sort.dto;

import jakarta.validation.constraints.NotNull;

public record SortRunRequest(
        @NotNull String algorithm,
        @NotNull int[] values) {
}
