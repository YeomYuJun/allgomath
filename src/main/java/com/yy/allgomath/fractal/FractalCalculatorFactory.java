package com.yy.allgomath.fractal;

import com.yy.allgomath.fractal.calculator.FractalCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 프랙탈 계산기들을 관리하는 팩토리 클래스
 */
@Component
public class FractalCalculatorFactory {
    
    private final Map<String, FractalCalculator> calculators;
    
    /**
     * Spring이 모든 FractalCalculator 구현체를 자동으로 주입
     */
    @Autowired
    public FractalCalculatorFactory(List<FractalCalculator> calculatorList) {
        this.calculators = new HashMap<>();
        
        // 각 계산기를 타입별로 등록
        for (FractalCalculator calculator : calculatorList) {
            calculators.put(calculator.getSupportedType().toLowerCase(), calculator);
        }
    }
    
    /**
     * 프랙탈 타입에 해당하는 계산기 반환
     * 
     * @param fractalType 프랙탈 타입 (mandelbrot, julia, sierpinski, barnsley 등)
     * @return 해당 타입의 계산기
     * @throws IllegalArgumentException 지원하지 않는 타입인 경우
     */
    public FractalCalculator getCalculator(String fractalType) {
        if (fractalType == null || fractalType.trim().isEmpty()) {
            throw new IllegalArgumentException("프랙탈 타입이 비어있습니다.");
        }
        
        FractalCalculator calculator = calculators.get(fractalType.toLowerCase());
        if (calculator == null) {
            throw new IllegalArgumentException("지원하지 않는 프랙탈 타입입니다: " + fractalType + 
                    ". 지원되는 타입: " + getSupportedTypes());
        }
        
        return calculator;
    }
    
    /**
     * 지원되는 모든 프랙탈 타입 목록 반환
     * 
     * @return 지원되는 프랙탈 타입 목록
     */
    public List<String> getSupportedTypes() {
        return calculators.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * 모든 계산기 정보 반환
     * 
     * @return 계산기 타입과 설명의 맵
     */
    public Map<String, String> getCalculatorInfo() {
        return calculators.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getDescription()
                ));
    }
    
    /**
     * 특정 타입이 지원되는지 확인
     * 
     * @param fractalType 확인할 프랙탈 타입
     * @return 지원 여부
     */
    public boolean isSupported(String fractalType) {
        return fractalType != null && 
               calculators.containsKey(fractalType.toLowerCase());
    }
    
    /**
     * 부드러운 색상을 지원하는 계산기 목록 반환
     * 
     * @return 부드러운 색상 지원 계산기 타입 목록
     */
    public List<String> getSmoothColoringSupportedTypes() {
        return calculators.entrySet().stream()
                .filter(entry -> entry.getValue().supportsSmoothColoring())
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }
}