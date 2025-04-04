package com.yy.allgomath.math.controller;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.math.model.Point3D;
import com.yy.allgomath.math.service.MathFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 수학 함수 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/math/functions")
public class MathFunctionController {

    private final MathFunctionService mathFunctionService;

    @Autowired
    public MathFunctionController(MathFunctionService mathFunctionService) {
        this.mathFunctionService = mathFunctionService;
    }

    /**
     * 지원되는 수학 함수 목록을 반환합니다.
     *
     * @return 지원되는 함수 목록
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSupportedFunctions() {
        Map<String, Object> response = new HashMap<>();
        response.put("functionTypes", mathFunctionService.getSupportedFunctionTypes());
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * 선택된 함수에 대한 3D 포인트 데이터를 생성합니다.
     *
     * @param xMin X 좌표의 최소값 (기본값: -5)
     * @param xMax X 좌표의 최대값 (기본값: 5)
     * @param yMin Y 좌표의 최소값 (기본값: -5)
     * @param yMax Y 좌표의 최대값 (기본값: 5)
     * @param resolution 해상도 (기본값: 20)
     * @param functionType 함수 유형 (기본값: standard)
     * @return 3D 포인트 목록
     */
    @GetMapping("/generate")
    public ResponseEntity<List<Point3D>> generateFunctionData(
            @RequestParam(defaultValue = "-5") double xMin,
            @RequestParam(defaultValue = "5") double xMax,
            @RequestParam(defaultValue = "-5") double yMin,
            @RequestParam(defaultValue = "5") double yMax,
            @RequestParam(defaultValue = "20") int resolution,
            @RequestParam(defaultValue = "standard") String functionType) {

        // 파라미터 검증
        if (xMin >= xMax) {
            throw new IllegalArgumentException("xMin은 xMax보다 작아야 합니다.");
        }
        if (yMin >= yMax) {
            throw new IllegalArgumentException("yMin은 yMax보다 작아야 합니다.");
        }
        if (resolution <= 0) {
            throw new IllegalArgumentException("해상도는 양수여야 합니다.");
        }

        List<Point3D> points = mathFunctionService.generateFunctionData(
                xMin, xMax, yMin, yMax, resolution, functionType);

        return ResponseEntity.ok(points);
    }

    /**
     * 안장점 함수에 대한 3D 포인트 데이터를 생성합니다 (기존 API와의 호환성을 위한 메서드).
     */
    @GetMapping("/gdd/anjang")
    public ResponseEntity<List<Point3D>> legacySaddleFunctionData(
            @RequestParam(defaultValue = "-5", name = "xMin") double xMin,
            @RequestParam(defaultValue = "5", name = "xMax") double xMax,
            @RequestParam(defaultValue = "-5", name = "yMin") double yMin,
            @RequestParam(defaultValue = "5", name = "yMax") double yMax,
            @RequestParam(defaultValue = "20", name = "resolution") int resolution,
            @RequestParam(defaultValue = "standard", name = "functionType") String functionType) {

        return generateFunctionData(xMin, xMax, yMin, yMax, resolution, functionType);
    }
}
