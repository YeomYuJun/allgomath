package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class FrequencySweepRequest {
    private List<Double> signal;
    private double samplingRate;
    private double minFrequency;
    private double maxFrequency;
    private int steps;

    // Constructors, Getters and Setters
    public FrequencySweepRequest() {}

    public List<Double> getSignal() { return signal; }
    public void setSignal(List<Double> signal) { this.signal = signal; }

    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }

    public double getMinFrequency() { return minFrequency; }
    public void setMinFrequency(double minFrequency) { this.minFrequency = minFrequency; }

    public double getMaxFrequency() { return maxFrequency; }
    public void setMaxFrequency(double maxFrequency) { this.maxFrequency = maxFrequency; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
}