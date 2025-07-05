package com.yy.allgomath.service;

import com.yy.allgomath.datatype.FractalResult;
import com.yy.allgomath.fractal.FractalCalculatorFactory;
import com.yy.allgomath.fractal.FractalParameters;
import com.yy.allgomath.fractal.calculator.FractalCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 개선된 프랙탈 서비스 - Strategy 패턴 적용
 */
@Service
public class FractalService {
    
    private final FractalCalculatorFactory calculatorFactory;
    
    @Autowired
    public FractalService(FractalCalculatorFactory calculatorFactory) {
        this.calculatorFactory = calculatorFactory;
    }
    
    /**
     * 통합 프랙탈 계산 메서드 (새로운 방식)
     * 
     * @param fractalType 프랙탈 타입
     * @param params 계산 파라미터
     * @return 프랙탈 결과
     */
    // 기존 메서드에 @Cacheable 추가
    // Caching 또한
    @Cacheable(value = "fractal",
            key = "#fractalType + '_' + #params.maxIterations + '_' + " +
                    "#params.width + '_' + #params.colorScheme + '_' + " +
                    "#params.smooth + '_' + T(java.util.Objects).hash(#params.xMin, #params.xMax, #params.yMin, #params.yMax)")
    public FractalResult calculateFractal(String fractalType, FractalParameters params) {
        FractalCalculator calculator = calculatorFactory.getCalculator(fractalType);
        double[][] values = calculator.calculate(params);
        
        return new FractalResult(params.getWidth(), params.getHeight(), values, 
                               params.getColorScheme(), params.isSmooth());
    }
    
    /**
     * 지원되는 프랙탈 타입 목록 반환
     */
    public List<String> getSupportedFractalTypes() {
        return calculatorFactory.getSupportedTypes();
    }
    
    /**
     * 프랙탈 계산기 정보 반환
     */
    public Map<String, String> getFractalCalculatorInfo() {
        return calculatorFactory.getCalculatorInfo();
    }
    
    /**
     * 부드러운 색상 지원 프랙탈 타입 목록
     */
    public List<String> getSmoothColoringSupportedTypes() {
        return calculatorFactory.getSmoothColoringSupportedTypes();
    }
    
    // ===========================================
    // 하위 호환성을 위한 기존 메서드들 (Deprecated)
    // ===========================================
    
    /**
     * @deprecated 새로운 calculateFractal 메서드 사용 권장
     */
    @Deprecated
    public FractalResult calculateMandelbrot(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations,
                                             String colorScheme, boolean smooth) {
        FractalParameters params = FractalParameters.mandelbrotDefaults()
                .xMin(xMin).xMax(xMax).yMin(yMin).yMax(yMax)
                .width(width).height(height)
                .maxIterations(maxIterations)
                .colorScheme(colorScheme)
                .smooth(smooth)
                .build();
        
        return calculateFractal("mandelbrot", params);
    }
    
    /**
     * @deprecated 새로운 calculateFractal 메서드 사용 권장
     */
    @Deprecated
    public FractalResult calculateJulia(double xMin, double xMax, double yMin, double yMax,
                                        double cReal, double cImag, int width, int height,
                                        int maxIterations, String colorScheme, boolean smooth) {
        FractalParameters params = FractalParameters.juliaDefaults()
                .xMin(xMin).xMax(xMax).yMin(yMin).yMax(yMax)
                .width(width).height(height)
                .maxIterations(maxIterations)
                .cReal(cReal).cImag(cImag)
                .colorScheme(colorScheme)
                .smooth(smooth)
                .build();
        
        return calculateFractal("julia", params);
    }
    
    /**
     * @deprecated 새로운 calculateFractal 메서드 사용 권장
     */
    @Deprecated
    public FractalResult calculateSierpinski(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations,
                                             String colorScheme, boolean smooth) {
        FractalParameters params = FractalParameters.sierpinskiDefaults()
                .xMin(xMin).xMax(xMax).yMin(yMin).yMax(yMax)
                .width(width).height(height)
                .maxIterations(maxIterations)
                .colorScheme(colorScheme)
                .smooth(smooth)
                .build();
        
        return calculateFractal("sierpinski", params);
    }
    
    /**
     * @deprecated 새로운 calculateFractal 메서드 사용 권장
     */
    @Deprecated
    public FractalResult calculateBarnsley(double xMin, double xMax, double yMin, double yMax,
                                          int width, int height, int maxIterations,
                                          String colorScheme, boolean smooth) {
        FractalParameters params = FractalParameters.barnsleyDefaults()
                .xMin(xMin).xMax(xMax).yMin(yMin).yMax(yMax)
                .width(width).height(height)
                .maxIterations(maxIterations)
                .colorScheme(colorScheme)
                .smooth(smooth)
                .build();
        
        return calculateFractal("barnsley", params);
    }
    
    // 기존 메서드들에 대한 하위 호환성 유지
    public FractalResult calculateMandelbrot(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations) {
        return calculateMandelbrot(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }

    public FractalResult calculateJulia(double xMin, double xMax, double yMin, double yMax,
                                        double cReal, double cImag, int width, int height,
                                        int maxIterations) {
        return calculateJulia(xMin, xMax, yMin, yMax, cReal, cImag, width, height, maxIterations, "classic", true);
    }

    public FractalResult calculateSierpinski(double xMin, double xMax, double yMin, double yMax,
                                             int width, int height, int maxIterations) {
        return calculateSierpinski(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }

    public FractalResult calculateBarnsley(double xMin, double xMax, double yMin, double yMax,
                                          int width, int height, int maxIterations) {
        return calculateBarnsley(xMin, xMax, yMin, yMax, width, height, maxIterations, "classic", true);
    }
    // 캐시 관리 메서드 추가
    @CacheEvict(value = "fractal", allEntries = true)
    public void clearAllCache() {
        // 전체 캐시 삭제
    }

    @CacheEvict(value = "fractal", key = "#fractalType + '_*'")
    public void clearCacheByType(String fractalType) {
        // 특정 타입 캐시만 삭제
    }
}