package com.yy.allgomath.fractal;

import com.yy.allgomath.fractal.dto.FractalResult;
import com.yy.allgomath.fractal.image.FractalImageEncoder;
import com.yy.allgomath.monitoring.AlgorithmPerformanceMetrics;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * 프랙탈 REST 컨트롤러. HTTP 변환 + 파라미터 검증 + 메트릭만 담당.
 * 비즈니스 로직은 {@link FractalService}, 이미지 인코딩은 {@link FractalImageEncoder}에 위임.
 */
@RestController
@RequestMapping("/api/fractal")
@Validated
@RequiredArgsConstructor
public class FractalController {

    private static final MediaType IMAGE_WEBP = MediaType.parseMediaType("image/webp");

    private final FractalService fractalService;
    private final FractalImageEncoder imageEncoder;
    private final AlgorithmPerformanceMetrics metrics;

    @GetMapping("/generate/image")
    public ResponseEntity<byte[]> generateFractalImage(
            @RequestParam String type,
            @RequestParam @Min(1) int iterations,
            @RequestParam @Min(1) int resolution,
            @RequestParam(defaultValue = "classic") String colorScheme,
            @RequestParam(defaultValue = "true") boolean smooth,
            @RequestParam(defaultValue = "0.0") double centerX,
            @RequestParam(defaultValue = "0.0") double centerY,
            @RequestParam(defaultValue = "1.0") double zoom,
            @RequestParam(required = false) Double juliaReal,
            @RequestParam(required = false) Double juliaImag) throws IOException {

        Timer.Sample sample = metrics.startFractalTimer();
        try {
            // 인자 순서는 /generate 와 동일하게 유지할 것 (positional, 두 호출부 동기화)
            FractalResult result = fractalService.generate(type, iterations, resolution, colorScheme,
                    smooth, centerX, centerY, zoom, juliaReal, juliaImag);
            byte[] webpData = imageEncoder.encodeToWebp(result.getPixels(), resolution, resolution);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(IMAGE_WEBP);
            headers.setContentLength(webpData.length);
            headers.setCacheControl("no-cache, no-store, max-age=0, must-revalidate");
            headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            headers.add("Access-Control-Expose-Headers", "Content-Type, Content-Length");
            headers.add("X-Content-Type-Options", "nosniff");
            return new ResponseEntity<>(webpData, headers, HttpStatus.OK);
        } finally {
            metrics.recordFractalTime(sample, type);
        }
    }

    @Timed(value = "fractal.api.response.time", description = "프랙탈 API 응답 시간")
    @GetMapping("/generate")
    public ResponseEntity<FractalResult> generateFractal(
            @RequestParam String type,
            @RequestParam @Min(1) int iterations,
            @RequestParam @Min(1) int resolution,
            @RequestParam(defaultValue = "classic") String colorScheme,
            @RequestParam(defaultValue = "true") boolean smooth,
            @RequestParam(defaultValue = "0.0") double centerX,
            @RequestParam(defaultValue = "0.0") double centerY,
            @RequestParam(defaultValue = "1.0") double zoom,
            @RequestParam(required = false) Double juliaReal,
            @RequestParam(required = false) Double juliaImag) {

        Timer.Sample sample = metrics.startFractalTimer();
        try {
            // 인자 순서는 /generate/image 와 동일하게 유지할 것 (positional, 두 호출부 동기화)
            FractalResult result = fractalService.generate(type, iterations, resolution, colorScheme,
                    smooth, centerX, centerY, zoom, juliaReal, juliaImag);
            return ResponseEntity.ok(result);
        } finally {
            metrics.recordFractalTime(sample, type);
        }
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getSupportedTypes() {
        Map<String, Object> response = Map.of(
                "supportedTypes", fractalService.getSupportedFractalTypes(),
                "calculatorInfo", fractalService.getFractalCalculatorInfo(),
                "smoothColoringTypes", fractalService.getSmoothColoringSupportedTypes()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-tile-cache")
    public ResponseEntity<Map<String, Object>> testTileCache() {
        return ResponseEntity.ok(fractalService.tileCacheBenchmark());
    }
}
