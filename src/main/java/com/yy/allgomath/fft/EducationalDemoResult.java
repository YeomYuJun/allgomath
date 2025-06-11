package com.yy.allgomath.fft;

import com.yy.allgomath.datatype.FrequencyDomainResult;
import com.yy.allgomath.datatype.TimeDomainSignal;

import java.util.List;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class EducationalDemoResult {
    private String demoType;
    private String description;
    private TimeDomainSignal originalSignal;
    private List<TimeDomainSignal> componentSignals;
    private FrequencyDomainResult fftResult;
    private List<String> explanations;

    public EducationalDemoResult(String demoType, String description, TimeDomainSignal originalSignal,
                                 List<TimeDomainSignal> componentSignals, FrequencyDomainResult fftResult,
                                 List<String> explanations) {
        this.demoType = demoType;
        this.description = description;
        this.originalSignal = originalSignal;
        this.componentSignals = componentSignals;
        this.fftResult = fftResult;
        this.explanations = explanations;
    }

    // Getters and Setters
    public String getDemoType() { return demoType; }
    public String getDescription() { return description; }
    public TimeDomainSignal getOriginalSignal() { return originalSignal; }
    public List<TimeDomainSignal> getComponentSignals() { return componentSignals; }
    public FrequencyDomainResult getFftResult() { return fftResult; }
    public List<String> getExplanations() { return explanations; }
}