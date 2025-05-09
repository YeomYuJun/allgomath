package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.GradientStep;
import com.yy.allgomath.datatype.Point3D;
import com.yy.allgomath.datatype.MinimumPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/convex")
public class ConvexFunctionController {

    @GetMapping("/surface")
    public ResponseEntity<List<Point3D>> getConvexFunctionData(
            @RequestParam(defaultValue = "-5", name = "xMin") double xMin,
            @RequestParam(defaultValue = "5", name = "xMax") double xMax,
            @RequestParam(defaultValue = "-5", name = "yMin") double yMin,
            @RequestParam(defaultValue = "5", name = "yMax") double yMax,
            @RequestParam(defaultValue = "20", name = "resolution") int resolution,
            @RequestParam(defaultValue = "quadratic", name = "functionType") String functionType) {

        List<Point3D> points = new ArrayList<>();
        double xStep = (xMax - xMin) / resolution;
        double yStep = (yMax - yMin) / resolution;

        for (int i = 0; i <= resolution; i++) {
            for (int j = 0; j <= resolution; j++) {
                double x = xMin + i * xStep;
                double y = yMin + j * yStep;
                double z = calculateZ(x, y, functionType);

                points.add(new Point3D(x, y, z));
            }
        }

        return ResponseEntity.ok(points);
    }

    /**
     * 경사 하강법을 사용하여 볼록 함수의 최소값을 찾습니다.
     *
     * @param startX 시작 x 좌표
     * @param startY 시작 y 좌표
     * @param functionType 함수 타입
     * @param learningRate 학습률
     * @param maxIterations 최대 반복 횟수
     * @param tolerance 수렴 허용 오차
     * @return 경사 하강법 경로와 최소값 정보
     */
    @GetMapping("/find-minimum")
    public ResponseEntity<Map<String, Object>> findMinimum(
            @RequestParam(name = "startX", defaultValue = "2.0") double startX,
            @RequestParam(name = "startY", defaultValue = "2.0") double startY,
            @RequestParam(defaultValue = "quadratic", name = "functionType") String functionType,
            @RequestParam(defaultValue = "0.1", name = "learningRate") double learningRate,
            @RequestParam(defaultValue = "100", name = "maxIterations") int maxIterations,
            @RequestParam(defaultValue = "1e-6", name = "tolerance") double tolerance) {

        List<GradientStep> path = new ArrayList<>();
        double currentX = startX;
        double currentY = startY;
        boolean converged = false;
        int finalStep = 0;

        for (int i = 0; i <= maxIterations; i++) {
            finalStep = i;
            double currentZ = calculateZ(currentX, currentY, functionType);
            double[] gradients = calculateGradient(currentX, currentY, functionType);
            double gradX = gradients[0];
            double gradY = gradients[1];
            double gradMagnitude = Math.sqrt(gradX * gradX + gradY * gradY);

            path.add(new GradientStep(i, currentX, currentY, currentZ, gradX, gradY));

            // 수렴 조건: 그래디언트 크기가 허용 오차보다 작으면 종료
            if (gradMagnitude < tolerance) {
                converged = true;
                break;
            }

            if (i == maxIterations) break; // 마지막 스텝

            // 경사 하강법 업데이트
            currentX = currentX - learningRate * gradX;
            currentY = currentY - learningRate * gradY;
        }

        // 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("minimumFound", new MinimumPoint(currentX, currentY,
                calculateZ(currentX, currentY, functionType)));
        result.put("converged", converged);
        result.put("iterations", finalStep);
        result.put("gradientMagnitude",
                Math.sqrt(Math.pow(path.get(finalStep).getGradientX(), 2) +
                        Math.pow(path.get(finalStep).getGradientY(), 2)));

        return ResponseEntity.ok(result);
    }

    /**
     * 함수의 모든 변수에 대한 특성을 분석합니다.
     *
     * @param functionType 함수 타입
     * @return 함수 특성 정보 (최소값 위치, 헤시안 행렬 고유값 등)
     */
    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeFunction(
            @RequestParam(defaultValue = "quadratic", name = "functionType") String functionType) {

        Map<String, Object> analysis = new HashMap<>();

        // 이론적 최소값 계산 (함수에 따라 다름)
        double[] theoreticalMinimum = calculateTheoreticalMinimum(functionType);
        analysis.put("theoreticalMinimumX", theoreticalMinimum[0]);
        analysis.put("theoreticalMinimumY", theoreticalMinimum[1]);
        analysis.put("theoreticalMinimumZ", theoreticalMinimum[2]);

        // 헤시안 행렬의 고유값 계산 (함수의 곡률 특성)
        double[] eigenvalues = calculateHessianEigenvalues(functionType);
        analysis.put("hessianEigenvalues", eigenvalues);
        analysis.put("isStrictlyConvex", eigenvalues[0] > 0 && eigenvalues[1] > 0);

        // 함수의 복잡도 측정 (변수 수, 차수 등)
        analysis.put("numberOfVariables", 2); // x, y
        analysis.put("degree", getFunctionDegree(functionType));
        analysis.put("functionName", getFunctionName(functionType));
        analysis.put("functionFormula", getFunctionFormula(functionType));

        return ResponseEntity.ok(analysis);
    }

    /**
     * 함수 타입에 따른 z 값(높이)을 계산합니다.
     *
     * @param x x 좌표
     * @param y y 좌표
     * @param functionType 함수 타입
     * @return 계산된 z 값
     */
    private double calculateZ(double x, double y, String functionType) {
        switch (functionType) {
            case "quadratic": //x
                // f(x,y) = x² + y² (기본 이차 볼록 함수)
                return Math.pow(x, 2) + Math.pow(y, 2);
            case "quartic":
                // f(x,y) = x⁴ + y⁴ + x²y² (사차 볼록 함수)
                return Math.pow(x, 4) + Math.pow(y, 4) + Math.pow(x*y, 2);
            case "exponential":
                // f(x,y) = e^(0.1*(x² + y²)) - 1
                return Math.exp(0.1 * (Math.pow(x, 2) + Math.pow(y, 2))) - 1;
            case "rosenbrock":
                // f(x,y) = 100(y - x²)² + (1 - x)² (Rosenbrock 함수)
                return 100 * Math.pow(y - Math.pow(x, 2), 2) + Math.pow(1 - x, 2);
            case "himmelblau":
                // f(x,y) = (x² + y - 11)² + (x + y² - 7)² (Himmelblau 함수)
                return Math.pow(Math.pow(x, 2) + y - 11, 2) + Math.pow(x + Math.pow(y, 2) - 7, 2);
            default:
                // 기본값: 이차 볼록 함수
                return Math.pow(x, 2) + Math.pow(y, 2);
        }
    }

    /**
     * 함수의 그래디언트를 계산합니다.
     *
     * @param x x 좌표
     * @param y y 좌표
     * @param functionType 함수 타입
     * @return [df/dx, df/dy] 형태의 그래디언트
     */
    private double[] calculateGradient(double x, double y, String functionType) {
        switch (functionType) {
            case "quadratic":
                // ∇f(x,y) = [2x, 2y]
                return new double[]{2 * x, 2 * y};
            case "quartic":
                // ∇f(x,y) = [4x³ + 2xy², 4y³ + 2x²y]
                return new double[]{
                        4 * Math.pow(x, 3) + 2 * x * Math.pow(y, 2),
                        4 * Math.pow(y, 3) + 2 * Math.pow(x, 2) * y
                };
            case "exponential":
                // ∇f(x,y) = [0.2x * e^(0.1*(x² + y²)), 0.2y * e^(0.1*(x² + y²))]
                double expTerm = Math.exp(0.1 * (Math.pow(x, 2) + Math.pow(y, 2)));
                return new double[]{0.2 * x * expTerm, 0.2 * y * expTerm};
            case "rosenbrock":
                // ∇f(x,y) = [-400x(y-x²) - 2(1-x), 200(y-x²)]
                return new double[]{
                        -400 * x * (y - Math.pow(x, 2)) - 2 * (1 - x),
                        200 * (y - Math.pow(x, 2))
                };
            case "himmelblau":
                // ∇f(x,y) = [2(x² + y - 11)2x + 2(x + y² - 7), 2(x² + y - 11) + 2(x + y² - 7)2y]
                double term1 = 2 * (Math.pow(x, 2) + y - 11);
                double term2 = 2 * (x + Math.pow(y, 2) - 7);
                return new double[]{
                        term1 * 2 * x + term2,
                        term1 + term2 * 2 * y
                };
            default:
                // 기본값: 이차 볼록 함수
                return new double[]{2 * x, 2 * y};
        }
    }

    /**
     * 함수의 이론적 최소값 위치를 계산합니다.
     *
     * @param functionType 함수 타입
     * @return [x_min, y_min, f(x_min,y_min)] 형태의 최소값 정보
     */
    private double[] calculateTheoreticalMinimum(String functionType) {
        switch (functionType) {
            case "quadratic":
                // 최소값: (0,0), 값: 0
                return new double[]{0, 0, 0};
            case "quartic":
                // 최소값: (0,0), 값: 0
                return new double[]{0, 0, 0};
            case "exponential":
                // 최소값: (0,0), 값: 0
                return new double[]{0, 0, 0};
            case "rosenbrock":
                // 최소값: (1,1), 값: 0
                return new double[]{1, 1, 0};
            case "himmelblau":
                // 여러 개의 지역 최소값 존재 (주된 최소값만 반환)
                return new double[]{3, 2, 0};
            default:
                // 기본값: 이차 볼록 함수
                return new double[]{0, 0, 0};
        }
    }

    /**
     * 함수의 헤시안 행렬 고유값을 계산합니다.
     *
     * @param functionType 함수 타입
     * @return 헤시안 행렬의 고유값
     */
    private double[] calculateHessianEigenvalues(String functionType) {
        // 단순화를 위해 유명한 함수들의 고유값을 하드코딩
        switch (functionType) {
            case "quadratic":
                return new double[]{2, 2};
            case "quartic":
                return new double[]{4, 4}; // 원점에서의 근사값
            case "exponential":
                return new double[]{0.2, 0.2}; // 원점에서의 근사값
            case "rosenbrock":
                return new double[]{802, 200}; // (1,1) 근처에서의 근사값
            case "himmelblau":
                return new double[]{42, 26}; // (3,2) 근처에서의 근사값
            default:
                return new double[]{2, 2};
        }
    }

    /**
     * 함수의 차수를 반환합니다.
     */
    private int getFunctionDegree(String functionType) {
        switch (functionType) {
            case "quadratic": return 2;
            case "quartic": return 4;
            case "exponential": return Integer.MAX_VALUE; // 무한 차수
            case "rosenbrock": return 4;
            case "himmelblau": return 4;
            default: return 2;
        }
    }

    /**
     * 함수의 이름을 반환합니다.
     */
    private String getFunctionName(String functionType) {
        switch (functionType) {
            case "quadratic": return "Quadratic Function";
            case "quartic": return "Quartic Function";
            case "exponential": return "Exponential Function";
            case "rosenbrock": return "Rosenbrock Function";
            case "himmelblau": return "Himmelblau Function";
            default: return "Unknown Function";
        }
    }

    /**
     * 함수의 수학적 표현식을 반환합니다.
     */
    private String getFunctionFormula(String functionType) {
        switch (functionType) {
            case "quadratic": return "f(x,y) = x² + y²";
            case "quartic": return "f(x,y) = x⁴ + y⁴ + x²y²";
            case "exponential": return "f(x,y) = e^(0.1*(x² + y²)) - 1";
            case "rosenbrock": return "f(x,y) = 100(y - x²)² + (1 - x)²";
            case "himmelblau": return "f(x,y) = (x² + y - 11)² + (x + y² - 7)²";
            default: return "Unknown Formula";
        }
    }
}