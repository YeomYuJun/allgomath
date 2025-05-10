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