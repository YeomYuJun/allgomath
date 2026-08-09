package com.yy.allgomath.fourier.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DFT 엔드포인트 HTTP 요청 DTO. */
public record FourierTransformRequest(
        @NotNull @Size(max = 4096) double[] signal,
        double sampleRate) {
}
