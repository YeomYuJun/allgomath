package com.yy.allgomath.fourier.dto;

import jakarta.validation.constraints.NotNull;

/** DFT 엔드포인트 HTTP 요청 DTO. */
public record FourierTransformRequest(
        @NotNull double[] signal,
        double sampleRate) {
}
