package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.datatype.Complex;
import com.yy.allgomath.fractal.FractalParameters;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * 만델브로 집합 계산기
 */
@Component
public class MandelbrotCalculator implements FractalCalculator {
    
    @Override
    public double[][] calculate(FractalParameters params) {
        validateParameters(params);
        
        double[][] values = new double[params.getHeight()][params.getWidth()];
        
        // 병렬 처리로 성능 최적화
        IntStream.range(0, params.getHeight()).parallel().forEach(y -> {
            for (int x = 0; x < params.getWidth(); x++) {
                double real = params.getXMin() + (params.getXMax() - params.getXMin()) * x / params.getWidth();
                double imag = params.getYMin() + (params.getYMax() - params.getYMin()) * y / params.getHeight();
                
                Complex c = new Complex(real, imag);
                
                if (params.isSmooth()) {
                    values[y][x] = calculateSmoothMandelbrot(c, params.getMaxIterations());
                } else {
                    values[y][x] = calculateMandelbrot(c, params.getMaxIterations());
                }
            }
        });
        
        return values;
    }
    
    @Override
    public String getSupportedType() {
        return "mandelbrot";
    }
    
    @Override
    public String getDescription() {
        return "만델브로 집합 - 복소수 c에 대해 z(n+1) = z(n)² + c 수열의 발산 여부를 계산";
    }
    
    /**
     * 일반적인 만델브로 계산 (정수 반복 횟수)
     */
    private double calculateMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
        int iteration = 0;
        
        while (iteration < maxIterations && z.magnitudeSquared() < 4.0) {
            z = z.multiply(z).add(c);
            iteration++;
        }
        
        return iteration == maxIterations ? -1 : iteration;
    }
    
    /**
     * 부드러운 만델브로 계산 (연속적인 값)
     */
    private double calculateSmoothMandelbrot(Complex c, int maxIterations) {
        Complex z = new Complex(0, 0);
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
     * 카디오이드와 구근 최적화 (성능 향상)
     * 만델브로 집합의 주요 구성 요소에 속하는 점들을 빠르게 식별
     */
    private boolean isInMainCardioidOrBulb(Complex c) {
        double x = c.getReal();
        double y = c.getImag();
        
        // 메인 카디오이드 체크
        double q = Math.pow(x - 0.25, 2) + y * y;
        if (q * (q + (x - 0.25)) < 0.25 * y * y) {
            return true;
        }
        
        // 좌측 구근 체크
        if (Math.pow(x + 1, 2) + y * y < 0.0625) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 최적화된 만델브로 계산 (선택적으로 사용 가능)
     */
    private double calculateOptimizedMandelbrot(Complex c, int maxIterations) {
        // 빠른 배제를 위한 사전 체크
        if (isInMainCardioidOrBulb(c)) {
            return -1; // 확실히 집합 내부
        }
        
        return calculateSmoothMandelbrot(c, maxIterations);
    }
}