package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.FractalResult;
import com.yy.allgomath.fractal.FractalParameters;
import com.yy.allgomath.fractal.calculator.FractalCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fractal")
public class FractalController {

    private final Map<String, FractalCalculator> calculators;

    @Autowired
    public FractalController(List<FractalCalculator> calculatorList) {
        // Strategy 패턴: 모든 FractalCalculator를 타입별로 Map에 저장
        this.calculators = calculatorList.stream()
                .collect(Collectors.toMap(
                    FractalCalculator::getSupportedType,
                    Function.identity()
                ));
    }

    /**
     * 통합 프랙탈 생성 API (Strategy 패턴 적용)
     *
     * @param type 프랙탈 타입 (mandelbrot, julia, sierpinski, barnsley)
     * @param iterations 반복 횟수
     * @param resolution 해상도
     * @param colorScheme 색상 스키마 (classic, rainbow, fire, ocean, grayscale)
     * @param smooth 부드러운 음영 적용 여부
     * @param centerX 중심 X 좌표
     * @param centerY 중심 Y 좌표
     * @param zoom 줌 레벨
     * @param juliaReal 줄리아 집합의 경우 사용할 실수부 (선택적)
     * @param juliaImag 줄리아 집합의 경우 사용할 허수부 (선택적)
     * @return ResponseEntity<FractalResult> 계산된 프랙탈 데이터와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/generate")
    public ResponseEntity<FractalResult> generateFractal(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "iterations") int iterations,
            @RequestParam(name = "resolution") int resolution,
            @RequestParam(name = "colorScheme", defaultValue = "classic") String colorScheme,
            @RequestParam(name = "smooth", defaultValue = "true") boolean smooth,
            @RequestParam(name = "centerX", defaultValue = "0.0") double centerX,
            @RequestParam(name = "centerY", defaultValue = "0.0") double centerY,
            @RequestParam(name = "zoom", defaultValue = "1.0") double zoom,
            @RequestParam(name = "juliaReal", required = false) Double juliaReal,
            @RequestParam(name = "juliaImag", required = false) Double juliaImag) {

        try {
            // 줌 레벨에 따른 범위 계산
            double range = 4.0 / zoom;
            double xMin = centerX - range/2;
            double xMax = centerX + range/2;
            double yMin = centerY - range/2;
            double yMax = centerY + range/2;

            // 프랙탈 타입에 따른 매개변수 빌더 생성
            FractalParameters.FractalParametersBuilder builder = FractalParameters.defaults()
                    .xMin(xMin).xMax(xMax)
                    .yMin(yMin).yMax(yMax)
                    .width(resolution).height(resolution)
                    .maxIterations(iterations)
                    .colorScheme(colorScheme)
                    .smooth(smooth);

            // 줄리아 집합 특별 처리
            if ("julia".equalsIgnoreCase(type)) {
                if (juliaReal == null || juliaImag == null) {
                    throw new IllegalArgumentException("줄리아 집합의 경우 juliaReal과 juliaImag 파라미터가 필요합니다.");
                }
                builder.cReal(juliaReal).cImag(juliaImag);
            }

            FractalParameters params = builder.build();
            
            // Strategy 패턴으로 계산기 선택 및 실행
            FractalCalculator calculator = getCalculator(type);
            double[][] values = calculator.calculate(params);
            
            FractalResult result = new FractalResult(resolution, resolution, values, colorScheme, smooth);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 지원되는 프랙탈 타입 목록 반환
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getSupportedTypes() {
        try {
            Map<String, Object> response = Map.of(
                "supportedTypes", calculators.keySet(),
                "calculatorInfo", calculators.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Map.of(
                            "description", entry.getValue().getDescription(),
                            "supportsSmoothColoring", entry.getValue().supportsSmoothColoring()
                        )
                    ))
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 계산기 조회 (타입별)
     */
    private FractalCalculator getCalculator(String type) {
        FractalCalculator calculator = calculators.get(type.toLowerCase());
        if (calculator == null) {
            throw new IllegalArgumentException("지원하지 않는 프랙탈 타입입니다: " + type);
        }
        return calculator;
    }
}