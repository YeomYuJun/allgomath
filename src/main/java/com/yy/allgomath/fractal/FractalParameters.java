package com.yy.allgomath.fractal;

import lombok.Builder;
import lombok.Data;

/**
 * 프랙탈 계산에 필요한 모든 파라미터를 담는 클래스
 */
@Data
@Builder
public class FractalParameters {
    // 좌표 범위
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    
    // 이미지 크기
    private int width;
    private int height;
    
    // 계산 설정
    private int maxIterations;
    private boolean smooth;
    
    // 색상 설정
    private String colorScheme;
    
    // 특수 파라미터 (줄리아 집합용)
    private Double cReal;
    private Double cImag;
    
    // 확장 가능한 추가 파라미터
    private java.util.Map<String, Object> additionalParams;
    
    /**
     * 기본 파라미터로 Builder 초기화
     */
    public static FractalParametersBuilder defaults() {
        return FractalParameters.builder()
                .xMin(-2.0)
                .xMax(2.0)
                .yMin(-2.0)
                .yMax(2.0)
                .width(800)
                .height(600)
                .maxIterations(100)
                .smooth(true)
                .colorScheme("classic")
                .additionalParams(new java.util.HashMap<>());
    }
    
    /**
     * 만델브로 집합 기본 설정
     */
    public static FractalParametersBuilder mandelbrotDefaults() {
        return defaults()
                .xMin(-2.5)
                .xMax(1.0)
                .yMin(-1.25)
                .yMax(1.25);
    }
    
    /**
     * 줄리아 집합 기본 설정
     */
    public static FractalParametersBuilder juliaDefaults() {
        return defaults()
                .cReal(-0.7)
                .cImag(0.27015);
    }
    
    /**
     * 시에르핀스키 삼각형 기본 설정
     */
    public static FractalParametersBuilder sierpinskiDefaults() {
        return defaults()
                .xMin(-3.0)
                .xMax(3.0)
                .yMin(-2.0)
                .yMax(4.0)
                .maxIterations(100000); // 점 생성 횟수
    }
    
    /**
     * 반슬리 고사리 기본 설정
     */
    public static FractalParametersBuilder barnsleyDefaults() {
        return defaults()
                .xMin(-3.0)
                .xMax(3.0)
                .yMin(0.0)
                .yMax(10.0)
                .maxIterations(1000000); // 점 생성 횟수
    }
}