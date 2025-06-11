package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class GeneratedSignalResult {
    private List<Double> timePoints;
    private List<Double> amplitudes;
    private double samplingRate;

    public GeneratedSignalResult(List<Double> timePoints, List<Double> amplitudes, double samplingRate) {
        this.timePoints = timePoints;
        this.amplitudes = amplitudes;
        this.samplingRate = samplingRate;
    }

    // Getters and Setters
    public List<Double> getTimePoints() { return timePoints; }
    public void setTimePoints(List<Double> timePoints) { this.timePoints = timePoints; }

    public List<Double> getAmplitudes() { return amplitudes; }
    public void setAmplitudes(List<Double> amplitudes) { this.amplitudes = amplitudes; }

    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }
}