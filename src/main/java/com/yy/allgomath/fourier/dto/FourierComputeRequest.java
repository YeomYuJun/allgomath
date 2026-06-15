package com.yy.allgomath.fourier.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FourierComputeRequest(
        @NotNull String wave,
        @Min(1) @Max(40) int N) {
}
