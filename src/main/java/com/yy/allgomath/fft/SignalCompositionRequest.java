package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class SignalCompositionRequest {
    private List<SignalGenerationRequest.FrequencyComponent> components;
    private double samplingRate;
    private double duration;
    private boolean includeNoise;
    private double noiseLevel;

    // Getters and Setters
    public List<SignalGenerationRequest.FrequencyComponent> getComponents() { return components; }
    public void setComponents(List<SignalGenerationRequest.FrequencyComponent> components) { this.components = components; }

    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public boolean isIncludeNoise() { return includeNoise; }
    public void setIncludeNoise(boolean includeNoise) { this.includeNoise = includeNoise; }

    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
}