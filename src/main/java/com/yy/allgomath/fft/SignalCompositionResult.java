package com.yy.allgomath.fft;

import com.yy.allgomath.datatype.FrequencyDomainResult;
import com.yy.allgomath.datatype.TimeDomainSignal;

import java.util.List;
import java.util.Map;

/**
 * [메서드 설명]
 *
 * @return [반환 값에 대한 설명]
 */
public class SignalCompositionResult {
    private TimeDomainSignal composedSignal;
    private List<TimeDomainSignal> individualComponents;
    private FrequencyDomainResult composedFFT;
    private List<FrequencyDomainResult> componentFFTs;
    private Map<String, Object> analysisResults;

    public SignalCompositionResult(TimeDomainSignal composedSignal, List<TimeDomainSignal> individualComponents,
                                   FrequencyDomainResult composedFFT, List<FrequencyDomainResult> componentFFTs,
                                   Map<String, Object> analysisResults) {
        this.composedSignal = composedSignal;
        this.individualComponents = individualComponents;
        this.composedFFT = composedFFT;
        this.componentFFTs = componentFFTs;
        this.analysisResults = analysisResults;
    }

    // Getters and Setters
    public TimeDomainSignal getComposedSignal() { return composedSignal; }
    public List<TimeDomainSignal> getIndividualComponents() { return individualComponents; }
    public FrequencyDomainResult getComposedFFT() { return composedFFT; }
    public List<FrequencyDomainResult> getComponentFFTs() { return componentFFTs; }
    public Map<String, Object> getAnalysisResults() { return analysisResults; }
}