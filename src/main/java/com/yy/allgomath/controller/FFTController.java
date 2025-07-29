package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.*;
import com.yy.allgomath.fft.*;
import com.yy.allgomath.service.FFTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fft")
public class FFTController {

    /*
     * 
     * analyze-realtime 에서 캐싱 전략으로 전환 테스트 결과 
     * 현재 FFT 연산에서 캐싱이 당장 필요해보이지 않는 것으로 결론..
     * freqeuncy-sweep 제외 20ms 정도 소요되는 것으로 보임
     */

    private final FFTService fftService;

    public FFTController(FFTService fftService) {
        this.fftService = fftService;
    }

    /**
     * 3Blue1Brown 스타일 신호 감기 시각화 데이터 생성
     */
    @PostMapping("/winding-visualization")
    public ResponseEntity<WindingVisualizationResult> getWindingVisualization(
            @RequestBody WindingRequest request) {

        WindingVisualizationResult result = fftService.computeWindingVisualization(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 다중 주파수 신호 생성
     */
    @PostMapping("/generate-signal")
    public ResponseEntity<GeneratedSignalResult> generateSignal(
            @RequestBody SignalGenerationRequest request) {

        TimeDomainSignal signal = fftService.generateMultiFrequencySignal(
            request.getFrequencies(),
            request.getAmplitudes(),
            request.getSamplingRate(),
            request.getDuration()
        );

        // 시간 포인트 생성
        List<Double> timePoints = new java.util.ArrayList<>();
        for (int i = 0; i < signal.getRealValues().length; i++) {
            timePoints.add(i / signal.getSamplingRate());
        }

        GeneratedSignalResult result = new GeneratedSignalResult(
            timePoints,
            java.util.Arrays.stream(signal.getRealValues()).boxed().collect(java.util.stream.Collectors.toList()),
            signal.getSamplingRate()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 주파수 스윕을 통한 완전한 FFT 시각화
     */
    @PostMapping("/frequency-sweep")
    public ResponseEntity<FFTService.FFTSweepResult> performFrequencySweep(
            @RequestBody FrequencySweepRequest request) {

        TimeDomainSignal signal = new TimeDomainSignal(
            request.getSignal().stream().mapToDouble(Double::doubleValue).toArray(),
            request.getSamplingRate()
        );

        FFTService.FFTSweepResult result = fftService.computeFrequencySweep(
            signal,
            request.getMinFrequency(),
            request.getMaxFrequency(),
            request.getSteps()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 실시간 FFT 분석 (기존 기능 향상)
     */
    @PostMapping("/analyze-realtime")
    public ResponseEntity<RealtimeFFTResult> analyzeRealtime(
            @RequestBody RealtimeAnalysisRequest request) {

        // 기본 FFT 계산
        TimeDomainSignal signal = new TimeDomainSignal(
            request.getSignal().stream().mapToDouble(Double::doubleValue).toArray(),
            request.getSamplingRate()
        );

        //캐싱 전략으로 전환
        //FrequencyDomainResult fftResult = fftService.computeFFT(signal);
        FrequencyDomainResult fftResult = fftService.computeFFTWithCache(signal);

        // 피크 검출
        List<FrequencyPeak> peaks = fftService.detectFrequencyPeaks(fftResult, request.getPeakThreshold());

        // 신호 특성 분석
        SignalCharacteristics characteristics = fftService.analyzeSignalCharacteristics(signal);

        RealtimeFFTResult result = new RealtimeFFTResult(
            fftResult,
            peaks,
            characteristics,
            System.currentTimeMillis()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 인터랙티브 교육용 데모 데이터 생성
     */
    @GetMapping("/educational-demo")
    public ResponseEntity<EducationalDemoResult> getEducationalDemo(
            @RequestParam(defaultValue = "basic") String demoType,
            @RequestParam(defaultValue = "100") double samplingRate,
            @RequestParam(defaultValue = "2") double duration) {

        EducationalDemoResult result = fftService.generateEducationalDemo(
            demoType, samplingRate, duration);

        return ResponseEntity.ok(result);
    }

    /**
     * 신호 합성 및 분해 시뮬레이션
     */
    @PostMapping("/signal-composition")
    public ResponseEntity<SignalCompositionResult> simulateSignalComposition(
            @RequestBody SignalCompositionRequest request) {

        SignalCompositionResult result = fftService.simulateSignalComposition(request);
        return ResponseEntity.ok(result);
    }
}
