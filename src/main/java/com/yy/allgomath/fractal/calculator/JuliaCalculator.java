package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.fractal.FractalParameters;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * 줄리아 집합 계산기
 */
@Component
public class JuliaCalculator implements FractalCalculator {

    @Cacheable(value = "julia",
            key = "T(java.util.Objects).hash(#params.xMin, #params.xMax, #params.yMin, #params.yMax, " +
                    "#params.width, #params.height, #params.maxIterations, #params.cReal, #params.cImag)")
    @Override
    public double[][] calculate(FractalParameters params) {
        validateParameters(params);
        validateJuliaParameters(params);
        
        double[][] values = new double[params.getHeight()][params.getWidth()];
        Complex c = new Complex(params.getCReal(), params.getCImag());
        
        // 병렬 처리로 성능 최적화
        IntStream.range(0, params.getHeight()).parallel().forEach(y -> {
            for (int x = 0; x < params.getWidth(); x++) {
                double real = params.getXMin() + (params.getXMax() - params.getXMin()) * x / params.getWidth();
                double imag = params.getYMin() + (params.getYMax() - params.getYMin()) * y / params.getHeight();
                
                Complex z = new Complex(real, imag);
                
                if (params.isSmooth()) {
                    values[y][x] = calculateSmoothJulia(z, c, params.getMaxIterations());
                } else {
                    values[y][x] = calculateJulia(z, c, params.getMaxIterations());
                }
            }
        });
        
        return values;
    }

    @Override
    public double[][] calculateWithCaching(FractalParameters params) {
        return new double[0][];
    }

    @Override
    public String getSupportedType() {
        return "julia";
    }
    
    @Override
    public String getDescription() {
        return "줄리아 집합 - 고정된 복소수 c에 대해 z(n+1) = z(n)² + c 수열의 발산 여부를 계산";
    }
    
    /**
     * 줄리아 집합 특화 파라미터 검증
     */
    private void validateJuliaParameters(FractalParameters params) {
        if (params.getCReal() == null || params.getCImag() == null) {
            throw new IllegalArgumentException("줄리아 집합 계산에는 cReal과 cImag 파라미터가 필요합니다.");
        }
    }
    
    /**
     * 일반적인 줄리아 계산 (정수 반복 횟수)
     */
    private double calculateJulia(Complex z, Complex c, int maxIterations) {
        int iteration = 0;
        
        while (iteration < maxIterations && z.magnitudeSquared() < 4.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }
        
        return iteration == maxIterations ? -1 : iteration;
    }
    
    /**
     * 부드러운 줄리아 계산 (연속적인 값)
     */
    private double calculateSmoothJulia(Complex z, Complex c, int maxIterations) {
        int iteration = 0;
        
        while (iteration < maxIterations && z.magnitudeSquared() < 256.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }
        
        if (iteration == maxIterations) {
            return -1; // 수렴하는 점
        }
        
        // 부드러운 반복 횟수 계산
        double logZn = Math.log(z.magnitudeSquared()) / 2.0;
        double nu = Math.log(logZn / Math.log(2)) / Math.log(2);
        
        return iteration + 1 - nu;
    }
    
    /**
     * 유명한 줄리아 집합 상수들
     */
    public static class FamousConstants {
        public static final Complex DRAGON = new Complex(-0.7, 0.27015);
        public static final Complex LIGHTNING = new Complex(-0.7269, 0.1889);
        public static final Complex SPIRAL = new Complex(-0.8, 0.156);
        public static final Complex RABBIT = new Complex(-0.123, 0.745);
        public static final Complex AIRPLANE = new Complex(-0.7269, 0.1889);
        public static final Complex DOUADY_RABBIT = new Complex(-0.123, 0.745);
        public static final Complex SAN_MARCO_DRAGON = new Complex(-0.75, 0.0);
        public static final Complex DENDRITE = new Complex(0.0, 1.0);
    }
    
    /**
     * 상수 이름으로 Complex 객체 반환
     */
    public static Complex getConstantByName(String name) {
        switch (name.toLowerCase()) {
            case "dragon": return FamousConstants.DRAGON;
            case "lightning": return FamousConstants.LIGHTNING;
            case "spiral": return FamousConstants.SPIRAL;
            case "rabbit": return FamousConstants.RABBIT;
            case "airplane": return FamousConstants.AIRPLANE;
            case "douady_rabbit": return FamousConstants.DOUADY_RABBIT;
            case "san_marco_dragon": return FamousConstants.SAN_MARCO_DRAGON;
            case "dendrite": return FamousConstants.DENDRITE;
            default: return FamousConstants.DRAGON;
        }
    }
}