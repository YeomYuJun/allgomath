package com.yy.allgomath.controller;

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
     * 함수 타입에 따른 z 값을 계산합니다.
     *
     * @param x x 좌표
     * @param y y 좌표
     * @param functionType 함수 타입 (standard, monkey, cubic)
     * @return 계산된 z 값
     */
    private double calculateZ(double x, double y, String functionType) {
        switch (functionType) {
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
}
