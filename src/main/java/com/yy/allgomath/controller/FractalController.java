package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.FractalResult;
import com.yy.allgomath.service.FractalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fractal")
public class FractalController {

    private FractalService fractalService;

    public FractalController(FractalService fractalService) {
        this.fractalService = fractalService;
    }

    /**
     * 주어진 범위, 해상도, 최대 반복 횟수를 이용하여 만델브로 집합을 계산하고 결과를 반환.
     *
     * @param xMin          실수부 최소값 (기본값: -2.0)
     * @param xMax          실수부 최대값 (기본값: 1.0)
     * @param yMin          허수부 최소값 (기본값: -1.5)
     * @param yMax          허수부 최대값 (기본값: 1.5)
     * @param width         결과 이미지의 너비 (기본값: 800)
     * @param height        결과 이미지의 높이 (기본값: 600)
     * @param maxIterations 각 점에 대한 최대 반복 횟수 (기본값: 100)
     * @return ResponseEntity<FractalResult> 계산된 만델브로 집합 데이터와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/mandelbrot")
    public ResponseEntity<FractalResult> getMandelbrotSet(
            @RequestParam(defaultValue = "-2.0", name = "xMin") double xMin,
            @RequestParam(defaultValue = "1.0", name = "xMax") double xMax,
            @RequestParam(defaultValue = "-1.5", name = "yMin") double yMin,
            @RequestParam(defaultValue = "1.5", name = "yMax") double yMax,
            @RequestParam(defaultValue = "800", name = "width") int width,
            @RequestParam(defaultValue = "600", name = "height") int height,
            @RequestParam(defaultValue = "100", name = "maxIterations") int maxIterations) {

        // 만델브로 집합 계산 로직 호출
        return ResponseEntity.ok(fractalService.calculateMandelbrot(
                xMin, xMax, yMin, yMax, width, height, maxIterations));
    }

    /**
     * 주어진 범위, 상수 c 값, 해상도, 최대 반복 횟수를 이용하여 줄리아 집합을 계산하고 결과를 반환.
     *
     * @param xMin          실수부 최소값 (기본값: -2.0)
     * @param xMax          실수부 최대값 (기본값: 2.0)
     * @param yMin          허수부 최소값 (기본값: -2.0)
     * @param yMax          허수부 최대값 (기본값: 2.0)
     * @param cReal         줄리아 집합 계산에 사용될 복소수 c의 실수부 (기본값: -0.7)
     * @param cImag         줄리아 집합 계산에 사용될 복소수 c의 허수부 (기본값: 0.27015)
     * @param width         결과 이미지의 너비 (기본값: 800)
     * @param height        결과 이미지의 높이 (기본값: 600)
     * @param maxIterations 각 점에 대한 최대 반복 횟수 (기본값: 100)
     * @return ResponseEntity<FractalResult> 계산된 줄리아 집합 데이터와 HTTP 상태 코드를 담은 응답
     */
    @GetMapping("/julia")
    public ResponseEntity<FractalResult> getJuliaSet(
            @RequestParam(defaultValue = "-2.0", name = "xMin") double xMin,
            @RequestParam(defaultValue = "2.0", name = "xMax") double xMax,
            @RequestParam(defaultValue = "-2.0", name = "yMin") double yMin,
            @RequestParam(defaultValue = "2.0", name = "yMax") double yMax,
            @RequestParam(defaultValue = "-0.7", name = "cReal") double cReal,
            @RequestParam(defaultValue = "0.27015", name = "cImag") double cImag,
            @RequestParam(defaultValue = "800", name = "width") int width,
            @RequestParam(defaultValue = "600", name = "height") int height,
            @RequestParam(defaultValue = "100", name = "maxIterations") int maxIterations) {

        /*desc
         c = -0.7 + 0.27015i는 흥미로운 패턴을 생성함.
         */

        // 줄리아 집합 계산 로직 호출
        return ResponseEntity.ok(fractalService.calculateJulia(
                xMin, xMax, yMin, yMax, cReal, cImag, width, height, maxIterations));
    }

    /**
     * 프론트엔드의 요구사항에 맞춘 통합 프랙탈 생성 API
     *
     * @param type          프랙탈 타입 (mandelbrot, julia, sierpinski, koch, barnsley)
     * @param iterations    반복 횟수
     * @param resolution    해상도
     * @param colorScheme   색상 스키마 (classic, rainbow, fire, ocean, grayscale)
     * @param smooth        부드러운 음영 적용 여부
     * @param centerX       중심 X 좌표
     * @param centerY       중심 Y 좌표
     * @param zoom          줌 레벨
     * @param juliaReal     줄리아 집합의 경우 사용할 실수부 (선택적)
     * @param juliaImag     줄리아 집합의 경우 사용할 허수부 (선택적)
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

        // 줌 레벨에 따른 범위 계산
        double range = 4.0 / zoom;
        double xMin = centerX - range/2;
        double xMax = centerX + range/2;
        double yMin = centerY - range/2;
        double yMax = centerY + range/2;

        // 프랙탈 타입에 따른 처리
        switch (type.toLowerCase()) {
            case "mandelbrot":
                return ResponseEntity.ok(fractalService.calculateMandelbrot(
                        xMin, xMax, yMin, yMax, resolution, resolution, iterations,
                        colorScheme, smooth));
            case "julia":
                if (juliaReal == null || juliaImag == null) {
                    throw new IllegalArgumentException("줄리아 집합의 경우 juliaReal과 juliaImag 파라미터가 필요합니다.");
                }
                return ResponseEntity.ok(fractalService.calculateJulia(
                        xMin, xMax, yMin, yMax, juliaReal, juliaImag, resolution, resolution, iterations,
                        colorScheme, smooth));
            case "sierpinski":
                return ResponseEntity.ok(fractalService.calculateSierpinski(
                        xMin, xMax, yMin, yMax, resolution, resolution, iterations,
                        colorScheme, smooth));
            case "barnsley":
                return ResponseEntity.ok(fractalService.calculateBarnsley(
                        xMin, xMax, yMin, yMax, resolution, resolution, iterations,
                        colorScheme, smooth));
            default:
                throw new IllegalArgumentException("지원하지 않는 프랙탈 타입입니다: " + type);
        }
    }
}