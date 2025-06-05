package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.fractal.FractalParameters;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 반슬리 고사리 계산기 (IFS - Iterated Function System)
 */
@Component
public class BarnsleyCalculator implements FractalCalculator {
    
    // IFS 변환 행렬과 확률
    private static final double[][] TRANSFORMS = {
        {0.0, 0.0, 0.0, 0.16, 0.0, 0.0, 0.01},      // 줄기
        {0.85, 0.04, -0.04, 0.85, 0.0, 1.6, 0.85},  // 큰 잎
        {0.2, -0.26, 0.23, 0.22, 0.0, 1.6, 0.07},   // 왼쪽 잎
        {-0.15, 0.28, 0.26, 0.24, 0.0, 0.44, 0.07}  // 오른쪽 잎
    };
    
    @Override
    public double[][] calculate(FractalParameters params) {
        validateParameters(params);
        
        double[][] hitCounts = new double[params.getHeight()][params.getWidth()];
        
        // IFS 알고리즘 실행
        performIFS(hitCounts, params);
        
        return hitCounts;
    }
    
    @Override
    public String getSupportedType() {
        return "barnsley";
    }
    
    @Override
    public String getDescription() {
        return "반슬리 고사리 - IFS(Iterated Function System)를 사용한 프랙탈 생성";
    }
    
    @Override
    public boolean supportsSmoothColoring() {
        return false; // 히트 카운트 기반이므로 smooth coloring 불필요
    }
    
    /**
     * IFS(Iterated Function System) 알고리즘 실행
     */
    private void performIFS(double[][] hitCounts, FractalParameters params) {
        Random random = new Random();
        
        // 시작점
        double x = 0.0;
        double y = 0.0;
        
        // 수렴을 위한 워밍업
        for (int i = 0; i < 100; i++) {
            int transformIndex = selectTransform(random);
            double[] result = applyTransform(x, y, transformIndex);
            x = result[0];
            y = result[1];
        }
        
        // 메인 반복
        for (int i = 0; i < params.getMaxIterations(); i++) {
            int transformIndex = selectTransform(random);
            double[] result = applyTransform(x, y, transformIndex);
            x = result[0];
            y = result[1];
            
            // 화면 좌표로 변환
            int pixelX = (int) ((x - params.getXMin()) / 
                               (params.getXMax() - params.getXMin()) * params.getWidth());
            int pixelY = (int) ((y - params.getYMin()) / 
                               (params.getYMax() - params.getYMin()) * params.getHeight());
            
            // 경계 검사 후 히트 카운트 증가
            if (pixelX >= 0 && pixelX < params.getWidth() && 
                pixelY >= 0 && pixelY < params.getHeight()) {
                hitCounts[pixelY][pixelX] += 1.0;
            }
        }
    }
    
    /**
     * 확률에 따라 변환 선택
     */
    private int selectTransform(Random random) {
        double r = random.nextDouble();
        double sum = 0.0;
        
        for (int i = 0; i < TRANSFORMS.length; i++) {
            sum += TRANSFORMS[i][6]; // 확률값
            if (r < sum) {
                return i;
            }
        }
        
        return TRANSFORMS.length - 1; // 기본값
    }
    
    /**
     * 아핀 변환 적용
     * [x'] = [a b] [x] + [e]
     * [y']   [c d] [y]   [f]
     */
    private double[] applyTransform(double x, double y, int transformIndex) {
        double[] transform = TRANSFORMS[transformIndex];
        
        double newX = transform[0] * x + transform[1] * y + transform[4];
        double newY = transform[2] * x + transform[3] * y + transform[5];
        
        return new double[]{newX, newY};
    }
}