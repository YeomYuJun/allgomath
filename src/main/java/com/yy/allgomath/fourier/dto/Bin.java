package com.yy.allgomath.fourier.dto;

/** DFT 스펙트럼 하나의 빈: 주파수와 진폭. */
public record Bin(double freq, double mag) {
}
