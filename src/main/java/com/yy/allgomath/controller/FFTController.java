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

    @PostMapping("/transform")
    public ResponseEntity<FrequencyDomainResult> transformSignal(
            @RequestBody TimeDomainSignal signal) {

        return ResponseEntity.ok(fftService.computeFFT(signal));
    }

    @PostMapping("/inverse")
    public ResponseEntity<TimeDomainSignal> inverseTransform(
            @RequestBody FrequencyDomainResult frequencyData) {

        return ResponseEntity.ok(fftService.computeInverseFFT(frequencyData));
    }

    @PostMapping("/visualization-steps")
    public ResponseEntity<List<FFTStep>> getFFTVisualizationSteps(
            @RequestBody TimeDomainSignal signal) {

        return ResponseEntity.ok(fftService.getFFTSteps(signal));
    }
}