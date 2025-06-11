package com.yy.allgomath.fft;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class SignalCharacteristics {
    private double totalEnergy;
    private double averageAmplitude;
    private double peakAmplitude;
    private double signalToNoiseRatio;
    private List<Double> dominantFrequencies;

    public SignalCharacteristics(double totalEnergy, double averageAmplitude, double peakAmplitude,
                                 double signalToNoiseRatio, List<Double> dominantFrequencies) {
        this.totalEnergy = totalEnergy;
        this.averageAmplitude = averageAmplitude;
        this.peakAmplitude = peakAmplitude;
        this.signalToNoiseRatio = signalToNoiseRatio;
        this.dominantFrequencies = dominantFrequencies;
    }

    // Getters and Setters
    public double getTotalEnergy() { return totalEnergy; }
    public double getAverageAmplitude() { return averageAmplitude; }
    public double getPeakAmplitude() { return peakAmplitude; }
    public double getSignalToNoiseRatio() { return signalToNoiseRatio; }
    public List<Double> getDominantFrequencies() { return dominantFrequencies; }
}