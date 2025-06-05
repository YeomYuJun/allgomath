package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.fractal.FractalParameters;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 시에르핀스키 삼각형 계산기 (Chaos Game 알고리즘)
 */
@Component
public class SierpinskiCalculator implements FractalCalculator {
    
    @Override
    public double[][] calculate(FractalParameters params) {
        validateParameters(params);
        
        double[][] hitCounts = new double[params.getHeight()][params.getWidth()];
        
        // 시에르핀스키 삼각형의 3개 꼭짓점 정의
        double centerX = (params.getXMax() + params.getXMin()) / 2;
        double centerY = (params.getYMax() + params.getYMin()) / 2;
        double size = Math.min(params.getXMax() - params.getXMin(), 
                              params.getYMax() - params.getYMin()) * 0.4;
        
        double[][] vertices = {
            {centerX, centerY + size},                                    // 상단
            {centerX - size * Math.sqrt(3)/2, centerY - size/2},        // 좌하단
            {centerX + size * Math.sqrt(3)/2, centerY - size/2}         // 우하단
        };
        
        // Chaos Game 알고리즘 실행
        performChaosGame(hitCounts, vertices, params);
        
        return hitCounts;
    }
    
    @Override
    public String getSupportedType() {
        return "sierpinski";
    }
    
    @Override
    public String getDescription() {
        return "시에르핀스키 삼각형 - Chaos Game 알고리즘을 사용한 프랙탈 생성";
    }
    
    @Override
    public boolean supportsSmoothColoring() {
        return false; // 시에르핀스키는 히트 카운트 기반이므로 smooth coloring이 의미 없음
    }
    
    /**
     * Chaos Game 알고리즘 실행
     */
    private void performChaosGame(double[][] hitCounts, double[][] vertices, FractalParameters params) {
        Random random = new Random();
        
        // 시작점 (삼각형 중심)
        double currentX = (vertices[0][0] + vertices[1][0] + vertices[2][0]) / 3;
        double currentY = (vertices[0][1] + vertices[1][1] + vertices[2][1]) / 3;
        
        // 수렴을 위한 워밍업
        for (int i = 0; i < 100; i++) {
            int vertexIndex = random.nextInt(3);
            currentX = (currentX + vertices[vertexIndex][0]) / 2.0;
            currentY = (currentY + vertices[vertexIndex][1]) / 2.0;
        }
        
        // 메인 반복
        for (int i = 0; i < params.getMaxIterations(); i++) {
            // 랜덤하게 꼭짓점 선택
            int vertexIndex = random.nextInt(3);
            
            // 현재 점과 선택된 꼭짓점의 중점으로 이동
            currentX = (currentX + vertices[vertexIndex][0]) / 2.0;
            currentY = (currentY + vertices[vertexIndex][1]) / 2.0;
            
            // 화면 좌표로 변환
            int pixelX = (int) ((currentX - params.getXMin()) / 
                               (params.getXMax() - params.getXMin()) * params.getWidth());
            int pixelY = (int) ((currentY - params.getYMin()) / 
                               (params.getYMax() - params.getYMin()) * params.getHeight());
            
            // 경계 검사 후 히트 카운트 증가
            if (pixelX >= 0 && pixelX < params.getWidth() && 
                pixelY >= 0 && pixelY < params.getHeight()) {
                hitCounts[pixelY][pixelX] += 1.0;
            }
        }
    }
    
    /**
     * 시에르핀스키 카펫 계산 (추가 기능)
     */
    public double[][] calculateCarpet(FractalParameters params, int depth) {
        double[][] values = new double[params.getHeight()][params.getWidth()];
        
        for (int y = 0; y < params.getHeight(); y++) {
            for (int x = 0; x < params.getWidth(); x++) {
                double real = params.getXMin() + (params.getXMax() - params.getXMin()) * x / params.getWidth();
                double imag = params.getYMin() + (params.getYMax() - params.getYMin()) * y / params.getHeight();
                
                // 정규화된 좌표 (0~1 범위)
                double normX = (real - params.getXMin()) / (params.getXMax() - params.getXMin());
                double normY = (imag - params.getYMin()) / (params.getYMax() - params.getYMin());
                
                values[y][x] = isSierpinskiCarpetPoint(normX, normY, depth) ? 1.0 : -1.0;
            }
        }
        
        return values;
    }
    
    /**
     * 시에르핀스키 카펫의 점 여부 판단
     */
    private boolean isSierpinskiCarpetPoint(double x, double y, int depth) {
        for (int i = 0; i < depth; i++) {
            x *= 3;
            y *= 3;
            
            if (((int)x % 3 == 1) && ((int)y % 3 == 1)) {
                return false;
            }
            
            x = x % 1;
            y = y % 1;
        }
        return true;
    }
}