package com.yy.allgomath.fourier.dto;

/** DFT 계산을 위한 내부 파라미터. */
public record FourierTransformParams(double[] signal, double sampleRate) {
}
