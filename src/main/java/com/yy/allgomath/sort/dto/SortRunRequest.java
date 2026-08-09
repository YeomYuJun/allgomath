package com.yy.allgomath.sort.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SortRunRequest(
        @NotNull String algorithm,
        @NotNull @Size(max = 128) int[] values) {
}
