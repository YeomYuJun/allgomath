package com.yy.allgomath.datatype;

import java.util.List;

/**
 * 신호 감기 요청 데이터
 */
public class WindingRequest {
    private List<Double> signal;           // 입력 신호
    private double samplingRate;           // 샘플링 레이트
    private double windingFrequency;       // 감는 주파수
    private double duration;               // 신호 지속 시간
    
    // Constructors, Getters and Setters
    public WindingRequest() {}
    
    public WindingRequest(List<Double> signal, double samplingRate, 
                         double windingFrequency, double duration) {
        this.signal = signal;
        this.samplingRate = samplingRate;
        this.windingFrequency = windingFrequency;
        this.duration = duration;
    }
    
    public List<Double> getSignal() { return signal; }
    public void setSignal(List<Double> signal) { this.signal = signal; }
    
    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }
    
    public double getWindingFrequency() { return windingFrequency; }
    public void setWindingFrequency(double windingFrequency) { this.windingFrequency = windingFrequency; }
    
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
}