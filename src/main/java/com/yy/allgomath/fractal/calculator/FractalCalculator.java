package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.fractal.FractalParameters;

/**
 * 프랙탈 계산을 위한 Strategy 인터페이스
 */
public interface FractalCalculator {
    
    /**
     * 프랙탈을 계산하여 각 픽셀의 값을 반환
     * 
     * @param params 프랙탈 계산 파라미터
     * @return 각 픽셀의 계산된 값 (double 배열)
     */
    double[][] calculate(FractalParameters params);


    /**
     * 프랙탈을 계산하여 각 픽셀의 값을 반환
     *
     * @param params 프랙탈 계산 파라미터
     * @return 각 픽셀의 계산된 값 (double 배열)
     */
    double[][] calculateWithCaching(FractalParameters params);

    
    /**
     * 이 계산기가 지원하는 프랙탈 타입 반환
     * 
     * @return 프랙탈 타입 이름
     */
    String getSupportedType();
    
    /**
     * 이 계산기의 설명 반환
     * 
     * @return 계산기 설명
     */
    default String getDescription() {
        return "프랙탈 계산기: " + getSupportedType();
    }
    
    /**
     * 이 계산기가 부드러운 음영을 지원하는지 여부
     * 
     * @return 부드러운 음영 지원 여부
     */
    default boolean supportsSmoothColoring() {
        return true;
    }
    
    /**
     * 파라미터 유효성 검사
     * 
     * @param params 검사할 파라미터
     * @throws IllegalArgumentException 파라미터가 유효하지 않은 경우
     */
    default void validateParameters(FractalParameters params) {
        if (params == null) {
            throw new IllegalArgumentException("파라미터가 null입니다.");
        }
        if (params.getWidth() <= 0 || params.getHeight() <= 0) {
            throw new IllegalArgumentException("이미지 크기는 양수여야 합니다.");
        }
        if (params.getMaxIterations() <= 0) {
            throw new IllegalArgumentException("최대 반복 횟수는 양수여야 합니다.");
        }
        if (params.getXMin() >= params.getXMax()) {
            throw new IllegalArgumentException("xMin은 xMax보다 작아야 합니다.");
        }
        if (params.getYMin() >= params.getYMax()) {
            throw new IllegalArgumentException("yMin은 yMax보다 작아야 합니다.");
        }
    }
}