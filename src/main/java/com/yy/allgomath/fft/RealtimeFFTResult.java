package com.yy.allgomath.fft;

import com.yy.allgomath.datatype.FrequencyDomainResult;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class RealtimeFFTResult {
    private FrequencyDomainResult fftResult;
    private List<FrequencyPeak> peaks;
    private SignalCharacteristics characteristics;
    private long timestamp;

    public RealtimeFFTResult(FrequencyDomainResult fftResult, List<FrequencyPeak> peaks,
                             SignalCharacteristics characteristics, long timestamp) {
        this.fftResult = fftResult;
        this.peaks = peaks;
        this.characteristics = characteristics;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public FrequencyDomainResult getFftResult() { return fftResult; }
    public List<FrequencyPeak> getPeaks() { return peaks; }
    public SignalCharacteristics getCharacteristics() { return characteristics; }
    public long getTimestamp() { return timestamp; }
}