package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class SignalGenerationRequest {
    private List<FrequencyComponent> components;
    private double samplingRate;
    private double duration;

    // Getters, Setters, Constructors
    public List<FrequencyComponent> getComponents() { return components; }
    public void setComponents(List<FrequencyComponent> components) { this.components = components; }

    public double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(double samplingRate) { this.samplingRate = samplingRate; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public List<Double> getFrequencies() {
        return components.stream().map(FrequencyComponent::getFrequency).collect(java.util.stream.Collectors.toList());
    }

    public List<Double> getAmplitudes() {
        return components.stream().map(FrequencyComponent::getAmplitude).collect(java.util.stream.Collectors.toList());
    }

    public static class FrequencyComponent {
        private double frequency;
        private double amplitude;

        public FrequencyComponent() {}
        public FrequencyComponent(double frequency, double amplitude) {
            this.frequency = frequency;
            this.amplitude = amplitude;
        }

        public double getFrequency() { return frequency; }
        public void setFrequency(double frequency) { this.frequency = frequency; }

        public double getAmplitude() { return amplitude; }
        public void setAmplitude(double amplitude) { this.amplitude = amplitude; }
    }
}