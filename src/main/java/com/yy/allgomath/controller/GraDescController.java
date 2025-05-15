package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.GradientStep;
import com.yy.allgomath.datatype.Point3D;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gdd")
public class GraDescController {

    @GetMapping("/anjang")
    public ResponseEntity<List<Point3D>> getSaddleFunctionData(
            @RequestParam(defaultValue = "-5", name = "xMin") double xMin,

            @RequestParam(defaultValue = "5", name = "xMax") double xMax,
            @RequestParam(defaultValue = "-5", name = "yMin") double yMin,
            @RequestParam(defaultValue = "5", name = "yMax") double yMax,
            @RequestParam(defaultValue = "20", name = "resolution") int resolution,
            @RequestParam(defaultValue = "standard", name = "functionType") String functionType) {

        List<Point3D> points = new ArrayList<>();
        double xStep = (xMax - xMin) / resolution;
        double yStep = (yMax - yMin) / resolution;

        for (int i = 0; i <= resolution; i++) {
            for (int j = 0; j <= resolution; j++) {
                double x = xMin + i * xStep;
                double y = yMin + j * yStep;
                // 안장점 함수: f(x,y) = x² - y²
                //double z = Math.pow(x, 2) - Math.pow(y, 2);
                double z = calculateZ(x, y, functionType);

                points.add(new Point3D(x, y, z));
            }
        }

        return ResponseEntity.ok(points);
    }
    /**
     * 경사 하강법 경로를 계산.
     *
     * @param startX 시작 x 좌표
     * @param startY 시작 y 좌표
     * @param functionType 함수 타입
     * @param learningRate 학습률
     * @param maxIterations 최대 반복 횟수
     * @return 경사 하강법 경로를 담은 GradientStep 리스트
     */
    @GetMapping("/gradient-descent-path") // 또는 @PostMapping("/gradient-descent-path")
    public ResponseEntity<List<GradientStep>> getGradientDescentPath(
            @RequestParam(name = "startX") double startX,
            @RequestParam(name = "startY") double startY,
            @RequestParam(defaultValue = "standard", name = "functionType") String functionType,
            @RequestParam(defaultValue = "0.1", name = "learningRate") double learningRate,
            @RequestParam(defaultValue = "100", name = "maxIterations") int maxIterations) {

        List<GradientStep> path = new ArrayList<>();
        double currentX = startX;
        double currentY = startY;

        for (int i = 0; i <= maxIterations; i++) {
            double currentZ = calculateZ(currentX, currentY, functionType);
            double[] gradients = calculateGradient(currentX, currentY, functionType);
            double gradX = gradients[0];
            double gradY = gradients[1];

            path.add(new GradientStep(i, currentX, currentY, currentZ, gradX, gradY));

            if (i == maxIterations) break; // 마지막 스텝은 위치만 기록하고 업데이트 안 함

            // 경사 하강법 업데이트 규칙
            currentX = currentX - learningRate * gradX;
            currentY = currentY - learningRate * gradY;

            // (선택적) 수렴 조건: 그래디언트 크기가 매우 작으면 중단
            if (Math.sqrt(gradX * gradX + gradY * gradY) < 1e-5) {
                // 다음 스텝 (수렴된 위치)을 한 번 더 기록하고 종료할 수도 있음
                currentZ = calculateZ(currentX, currentY, functionType);
                path.add(new GradientStep(i + 1, currentX, currentY, currentZ, 0, 0)); // 그래디언트 0으로 표시
                break;
            }
        }
        return ResponseEntity.ok(path);
    }


    /**
     * 함수 타입에 따른 z 값을 계산합니다.
     *
     * @param x x 좌표
     * @param y y 좌표
     * @param functionType 함수 타입 (standard, monkey, cubic)
     * @return 계산된 z 값
     */
    private double calculateZ(double x, double y, String functionType) {
        switch (functionType) {
            case "harf" :
                return 0.5 * Math.pow(x, 2) - 0.5 * Math.pow(y, 2);
            case "standard":
                // f(x,y) = x² - y²
                return Math.pow(x, 2) - Math.pow(y, 2);
            case "monkey":
                // f(x,y) = x³ - 3xy²
                return Math.pow(x, 3) - 3 * x * Math.pow(y, 2);
            case "cubic":
                // f(x,y) = x⁴ - y⁴
                return Math.pow(x, 4) - Math.pow(y, 4);
            case "triangle":
                // f(x,y) = x5 - y5
                return Math.pow(x, 5) - Math.pow(y, 5);
            default:
                // 기본값: standard
                return Math.pow(x, 2) - Math.pow(y, 2);
        }
    }

    /**
     * 주어진 (x, y)에서 함수 f(x,y)의 그래디언트(경사도)를 계산합니다.
     * @param x x 좌표
     * @param y y 좌표
     * @param functionType 함수 타입
     * @return [df/dx, df/dy] 형태의 double 배열
     */
    private double[] calculateGradient(double x, double y, String functionType) {
        double gradX, gradY;
        switch (functionType) {
            case "harf":  // f(x,y) = 1/2x² - 1/2y²
                gradX = x;      // df/dx = 1/2 * 2x
                gradY = -1 * y;      // df/dy = -1/2 * 2y
                break;
            case "standard": // f(x,y) = x² - y²
                gradX = 2 * x;      // df/dx = 2x
                gradY = -2 * y;     // df/dy = -2y
                break;
            case "monkey":   // f(x,y) = x³ - 3xy²
                gradX = 3 * Math.pow(x, 2) - 3 * Math.pow(y, 2); // df/dx = 3x² - 3y²
                gradY = -6 * x * y;                              // df/dy = -6xy
                break;
            case "cubic":    // f(x,y) = x⁴ - y⁴
                gradX = 4 * Math.pow(x, 3); // df/dx = 4x³
                gradY = -4 * Math.pow(y, 3);// df/dy = -4y³
                break;
            case "triangle": // f(x,y) = x⁵ - y⁵
                gradX = 5 * Math.pow(x, 4); // df/dx = 5x⁴
                gradY = -5 * Math.pow(y, 4);// df/dy = -5y⁴
                break;
            default: // 기본값: standard
                gradX = 2 * x;
                gradY = -2 * y;
                break;
        }
        return new double[]{gradX, gradY};
    }
}
