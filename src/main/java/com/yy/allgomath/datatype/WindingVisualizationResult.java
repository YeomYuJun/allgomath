package com.yy.allgomath.datatype;

import java.util.List;

/**
 * 3Blue1Brown 스타일 푸리에 변환 시각화를 위한 데이터 구조
 */
public class WindingVisualizationResult {
    private List<WindingPoint> windingPath;        // 복소평면에서 감긴 경로
    private CenterOfMass centerOfMass;             // 질량 중심
    private double windingFrequency;               // 감는 주파수
    private List<Double> timePoints;               // 시간 점들
    private List<Double> originalSignal;           // 원본 신호
    
    public WindingVisualizationResult() {}
    
    public WindingVisualizationResult(List<WindingPoint> windingPath, 
                                    CenterOfMass centerOfMass,
                                    double windingFrequency,
                                    List<Double> timePoints,
                                    List<Double> originalSignal) {
        this.windingPath = windingPath;
        this.centerOfMass = centerOfMass;
        this.windingFrequency = windingFrequency;
        this.timePoints = timePoints;
        this.originalSignal = originalSignal;
    }
    
    // Getters and Setters
    public List<WindingPoint> getWindingPath() { return windingPath; }
    public void setWindingPath(List<WindingPoint> windingPath) { this.windingPath = windingPath; }
    
    public CenterOfMass getCenterOfMass() { return centerOfMass; }
    public void setCenterOfMass(CenterOfMass centerOfMass) { this.centerOfMass = centerOfMass; }
    
    public double getWindingFrequency() { return windingFrequency; }
    public void setWindingFrequency(double windingFrequency) { this.windingFrequency = windingFrequency; }
    
    public List<Double> getTimePoints() { return timePoints; }
    public void setTimePoints(List<Double> timePoints) { this.timePoints = timePoints; }
    
    public List<Double> getOriginalSignal() { return originalSignal; }
    public void setOriginalSignal(List<Double> originalSignal) { this.originalSignal = originalSignal; }
    
    /**
     * 복소평면에서 감긴 경로의 한 점
     */
    public static class WindingPoint {
        private double real;           // 실수부 (복소평면 X축)
        private double imaginary;      // 허수부 (복소평면 Y축)
        private double time;           // 시간
        private double amplitude;      // 원본 신호 진폭
        
        public WindingPoint(double real, double imaginary, double time, double amplitude) {
            this.real = real;
            this.imaginary = imaginary;
            this.time = time;
            this.amplitude = amplitude;
        }
        
        // Getters and Setters
        public double getReal() { return real; }
        public void setReal(double real) { this.real = real; }
        
        public double getImaginary() { return imaginary; }
        public void setImaginary(double imaginary) { this.imaginary = imaginary; }
        
        public double getTime() { return time; }
        public void setTime(double time) { this.time = time; }
        
        public double getAmplitude() { return amplitude; }
        public void setAmplitude(double amplitude) { this.amplitude = amplitude; }
    }
    
    /**
     * 질량 중심 정보
     */
    public static class CenterOfMass {
        private double real;           // 질량 중심의 실수부
        private double imaginary;      // 질량 중심의 허수부
        private double magnitude;      // 원점으로부터의 거리
        private double phase;          // 위상
        
        public CenterOfMass(double real, double imaginary) {
            this.real = real;
            this.imaginary = imaginary;
            this.magnitude = Math.sqrt(real * real + imaginary * imaginary);
            this.phase = Math.atan2(imaginary, real);
        }
        
        // Getters and Setters
        public double getReal() { return real; }
        public void setReal(double real) { this.real = real; }
        
        public double getImaginary() { return imaginary; }
        public void setImaginary(double imaginary) { this.imaginary = imaginary; }
        
        public double getMagnitude() { return magnitude; }
        public void setMagnitude(double magnitude) { this.magnitude = magnitude; }
        
        public double getPhase() { return phase; }
        public void setPhase(double phase) { this.phase = phase; }
    }
}