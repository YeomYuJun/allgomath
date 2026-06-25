package com.yy.allgomath.fourier.dto;

/** DFT 결과: 단측 스펙트럼과 감지된 피크 주파수 목록. */
public record FourierTransformResult(Bin[] spectrum, double[] peaks) {
}
