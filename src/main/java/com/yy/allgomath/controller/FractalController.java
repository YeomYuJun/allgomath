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
}