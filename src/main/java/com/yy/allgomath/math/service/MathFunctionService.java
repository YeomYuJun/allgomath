package com.yy.allgomath.math.service;

import com.yy.allgomath.common.constants.AlgorithmConstants;
import com.yy.allgomath.math.model.Point3D;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 수학 함수 관련 기능을 제공하는 서비스 클래스
 */
@Service
public class MathFunctionService {

    /**
     * 지정된 범위와 해상도에 따라 선택된 함수의 3D 포인트를 생성합니다.
     *
     * @param xMin        X 좌표의 최소값
     * @param xMax        X 좌표의 최대값
     * @param yMin        Y 좌표의 최소값
     * @param yMax        Y 좌표의 최대값
     * @param resolution  각 축의 포인트 수
     * @param functionType 생성할 함수 유형
     * @return 3D 포인트 목록
     */
    public List<Point3D> generateFunctionData(
            double xMin, double xMax, double yMin, double yMax, 
            int resolution, String functionType) {
        
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

        return points;
    }
    
    /**
     * 함수 타입에 따른 z 값을 계산합니다.
     *
     * @param x 입력 x 좌표
     * @param y 입력 y 좌표
     * @param functionType 함수 타입
     * @return 계산된 z 값
     */
    private double calculateZ(double x, double y, String functionType) {
        switch (functionType) {
            case AlgorithmConstants.FUNCTION_TYPE_STANDARD:
                // f(x,y) = x² - y²
                return Math.pow(x, 2) - Math.pow(y, 2);
                
            case AlgorithmConstants.FUNCTION_TYPE_MONKEY:
                // f(x,y) = x³ - 3xy²
                return Math.pow(x, 3) - 3 * x * Math.pow(y, 2);
                
            case AlgorithmConstants.FUNCTION_TYPE_CUBIC:
                // f(x,y) = x⁴ - y⁴
                return Math.pow(x, 4) - Math.pow(y, 4);
                
            case AlgorithmConstants.FUNCTION_TYPE_TRIANGLE:
                // f(x,y) = x⁵ - y⁵
                return Math.pow(x, 5) - Math.pow(y, 5);
                
            default:
                // 기본값: standard
                return Math.pow(x, 2) - Math.pow(y, 2);
        }
    }
    
    /**
     * 지원되는 함수 유형 목록을 반환합니다.
     *
     * @return 지원되는 함수 유형 목록
     */
    public List<String> getSupportedFunctionTypes() {
        List<String> types = new ArrayList<>();
        types.add(AlgorithmConstants.FUNCTION_TYPE_STANDARD);
        types.add(AlgorithmConstants.FUNCTION_TYPE_MONKEY);
        types.add(AlgorithmConstants.FUNCTION_TYPE_CUBIC);
        types.add(AlgorithmConstants.FUNCTION_TYPE_TRIANGLE);
        return types;
    }
}
