package com.yy.allgomath.fractal;

import com.yy.allgomath.common.exception.InvalidParameterException;
import com.yy.allgomath.fractal.calculator.FractalCalculator;
import com.yy.allgomath.fractal.dto.FractalParameters;
import com.yy.allgomath.fractal.dto.FractalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 프랙탈 도메인 오케스트레이션.
 * 파라미터 조립 + 계산기 위임을 담당하며, 계산/캐시는 calculateWithCaching 경로(mandelbrot_tile/julia)에 위임한다.
 */
@Service
@RequiredArgsConstructor
public class FractalService {

    private final FractalCalculatorFactory calculatorFactory;

    /**
     * 통합 프랙탈 생성. 컨트롤러 쿼리 파라미터로부터 FractalParameters를 조립하여 계산한다.
     */
    public FractalResult generate(String type, int iterations, int resolution,
                                  String colorScheme, boolean smooth,
                                  double centerX, double centerY, double zoom,
                                  Double juliaReal, Double juliaImag) {
        double range = 4.0 / zoom;
        FractalParameters.FractalParametersBuilder builder = FractalParameters.defaults()
                .xMin(centerX - range / 2).xMax(centerX + range / 2)
                .yMin(centerY - range / 2).yMax(centerY + range / 2)
                .width(resolution).height(resolution)
                .maxIterations(iterations)
                .colorScheme(colorScheme)
                .smooth(smooth);

        if ("julia".equalsIgnoreCase(type)) {
            if (juliaReal == null || juliaImag == null) {
                throw new InvalidParameterException("줄리아 집합의 경우 juliaReal과 juliaImag 파라미터가 필요합니다.");
            }
            builder.cReal(juliaReal).cImag(juliaImag);
        }

        FractalParameters params = builder.build();
        FractalCalculator calculator = calculatorFactory.getCalculator(type); // 미지원 타입이면 예외(->400)
        double[][] values = calculator.calculateWithCaching(params);
        return new FractalResult(resolution, resolution, values, colorScheme, smooth);
    }

    public List<String> getSupportedFractalTypes() {
        return calculatorFactory.getSupportedTypes();
    }

    public Map<String, String> getFractalCalculatorInfo() {
        return calculatorFactory.getCalculatorInfo();
    }

    public List<String> getSmoothColoringSupportedTypes() {
        return calculatorFactory.getSmoothColoringSupportedTypes();
    }

    /**
     * 타일 캐시 벤치마크(디버그용 /test-tile-cache 지원). 동일 파라미터 2회 호출 시간 비교.
     */
    public Map<String, Object> tileCacheBenchmark() {
        FractalParameters params = FractalParameters.mandelbrotDefaults()
                .width(400).height(400).maxIterations(50).smooth(true).build();
        FractalCalculator calculator = calculatorFactory.getCalculator("mandelbrot");

        long s1 = System.currentTimeMillis();
        calculator.calculateWithCaching(params);
        long miss = System.currentTimeMillis() - s1;

        long s2 = System.currentTimeMillis();
        calculator.calculateWithCaching(params);
        long hit = System.currentTimeMillis() - s2;

        return Map.of(
                "cacheMissTime", miss,
                "cacheHitTime", hit,
                "improvement", String.format("%.1f%%", miss == 0 ? 0.0 : (1 - (double) hit / miss) * 100),
                "tileCount", (400 / 32 + 1) * (400 / 32 + 1)
        );
    }
}
