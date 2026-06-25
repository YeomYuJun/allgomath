package com.yy.allgomath.fourier;

import com.yy.allgomath.fourier.dto.FourierComputeRequest;
import com.yy.allgomath.fourier.dto.FourierParams;
import com.yy.allgomath.fourier.dto.FourierResult;
import com.yy.allgomath.fourier.dto.FourierTransformParams;
import com.yy.allgomath.fourier.dto.FourierTransformRequest;
import com.yy.allgomath.fourier.dto.FourierTransformResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Fourier 급수 계수 API. HTTP 변환/검증만, 연산은 {@link FourierService}. */
@RestController
@RequestMapping("/api/algorithms/fourier")
@RequiredArgsConstructor
public class FourierController {

    private final FourierService fourierService;
    private final FourierTransformService fourierTransformService;

    @PostMapping("/series")
    public ResponseEntity<FourierResult> series(@Valid @RequestBody FourierComputeRequest req) {
        return ResponseEntity.ok(fourierService.compute(new FourierParams(req.wave(), req.N())));
    }

    @PostMapping("/transform")
    public ResponseEntity<FourierTransformResult> transform(@Valid @RequestBody FourierTransformRequest req) {
        return ResponseEntity.ok(fourierTransformService.compute(
                new FourierTransformParams(req.signal(), req.sampleRate())));
    }
}
