package com.yy.allgomath.bezier;

import com.yy.allgomath.bezier.dto.BezierComputeRequest;
import com.yy.allgomath.bezier.dto.BezierParams;
import com.yy.allgomath.bezier.dto.BezierResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Bézier 곡선 API. HTTP 변환/검증만, 연산은 {@link BezierService}. */
@RestController("bezierDomainController")
@RequestMapping("/api/algorithms/bezier")
@RequiredArgsConstructor
public class BezierController {

    private final BezierService bezierService;

    @PostMapping("/compute")
    public ResponseEntity<BezierResult> compute(@Valid @RequestBody BezierComputeRequest req) {
        return ResponseEntity.ok(bezierService.compute(new BezierParams(req.controlPoints(), req.samples())));
    }
}
