package com.yy.allgomath.fft;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class FrequencyPeak {
    private double frequency;
    private double magnitude;
    private double phase;
    private double sharpness;

    public FrequencyPeak(double frequency, double magnitude, double phase, double sharpness) {
        this.frequency = frequency;
        this.magnitude = magnitude;
        this.phase = phase;
        this.sharpness = sharpness;
    }

    // Getters and Setters
    public double getFrequency() { return frequency; }
    public double getMagnitude() { return magnitude; }
    public double getPhase() { return phase; }
    public double getSharpness() { return sharpness; }
}