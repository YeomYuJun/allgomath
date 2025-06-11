package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class RealtimeAnalysisRequest {
    private List<Double> signal;
    private double samplingRate;
    private double peakThreshold;

    // Constructors, Getters and Setters
    public RealtimeAnalysisRequest() {}

    public List<Double> getSignal() { return signal; }
    public void setSignal(List<Double> signal) { this.signal = signal; }

    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }

    public double getPeakThreshold() { return peakThreshold; }
    public void setPeakThreshold(double peakThreshold) { this.peakThreshold = peakThreshold; }
}