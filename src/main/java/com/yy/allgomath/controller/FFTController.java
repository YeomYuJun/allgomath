package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.FFTStep;
import com.yy.allgomath.datatype.FrequencyDomainResult;
import com.yy.allgomath.datatype.TimeDomainSignal;
import com.yy.allgomath.service.FFTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fft")
public class FFTController {

    private FFTService fftService;

    public FFTController(FFTService fftService) {
        this.fftService = fftService;
    }

    /**
     * 시간 영역 신호를 입력받아 고속 푸리에 변환(FFT)을 수행하고, 주파수 영역 결과를 반환.
     *
     * @param signal 변환할 시간 영역 신호 데이터
     * @return ResponseEntity<FrequencyDomainResult> 주파수 영역 분석 결과와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/transform")
    public ResponseEntity<FrequencyDomainResult> transformSignal(
            @RequestBody TimeDomainSignal signal) {

        return ResponseEntity.ok(fftService.computeFFT(signal));
    }

    /**
     * 주파수 영역 데이터를 입력받아 역 고속 푸리에 변환(Inverse FFT)을 수행하고, 시간 영역 신호를 복원하여 반환
     *
     * @param frequencyData 역변환할 주파수 영역 데이터
     * @return ResponseEntity<TimeDomainSignal> 복원된 시간 영역 신호 데이터와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/inverse")
    public ResponseEntity<TimeDomainSignal> inverseTransform(
            @RequestBody FrequencyDomainResult frequencyData) {

        return ResponseEntity.ok(fftService.computeInverseFFT(frequencyData));
    }

    /**
     * 시간 영역 신호를 입력받아 고속 푸리에 변환(FFT)의 각 단계를 시각화하기 위한 데이터를 반환.
     *
     * @param signal 변환 과정을 시각화할 시간 영역 신호 데이터
     * @return ResponseEntity<List<FFTStep>> FFT 변환의 각 단계를 담은 리스트와 HTTP 상태 코드를 담은 응답
     */
    @PostMapping("/visualization-steps")
    public ResponseEntity<List<FFTStep>> getFFTVisualizationSteps(
            @RequestBody TimeDomainSignal signal) {

        return ResponseEntity.ok(fftService.getFFTSteps(signal));
    }
}