package com.yy.allgomath.datatype;

// 주파수 도메인 결과 모델
public class FrequencyDomainResult {
    private double[] frequencies;
    private double[] magnitudes;
    private double[] phases;

    public FrequencyDomainResult() {}

    public FrequencyDomainResult(double[] frequencies, double[] magnitudes, double[] phases) {
        this.frequencies = frequencies;
        this.magnitudes = magnitudes;
        this.phases = phases;
    }

    // Getter와 Setter 메서드
    public double[] getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(double[] frequencies) {
        this.frequencies = frequencies;
    }

    public double[] getMagnitudes() {
        return magnitudes;
    }

    public void setMagnitudes(double[] magnitudes) {
        this.magnitudes = magnitudes;
    }

    public double[] getPhases() {
        return phases;
    }

    public void setPhases(double[] phases) {
        this.phases = phases;
    }
}