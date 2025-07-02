package com.yy.allgomath.cache.service;

import com.yy.allgomath.fractal.FractalCalculatorFactory;
import com.yy.allgomath.fractal.FractalParameters;
import com.yy.allgomath.fractal.calculator.FractalCalculator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CachedFractalService {

    private final FractalCalculatorFactory calculatorFactory;

    public CachedFractalService(FractalCalculatorFactory calculatorFactory) {
        this.calculatorFactory = calculatorFactory;
    }

    @Cacheable(value = "fractal", key = "#type + '_' + #params.maxIterations + '_' + #params.width + '_' + #params.colorScheme")
    public double[][] calculateFractal(String type, FractalParameters params) {
        // 기존 계산 로직
        FractalCalculator calculator = calculatorFactory.getCalculator(type);
        return calculator.calculate(params);
    }

    @CacheEvict(value = "fractal", allEntries = true)
    public void clearFractalCache() {
        // 캐시 전체 삭제
    }
}