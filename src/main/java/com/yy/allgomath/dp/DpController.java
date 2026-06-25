package com.yy.allgomath.dp;

import com.yy.allgomath.dp.dto.DpParams;
import com.yy.allgomath.dp.dto.DpResult;
import com.yy.allgomath.dp.dto.DpSolveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DP 격자 경로 API. HTTP 변환/검증만, 연산은 {@link DpService}. */
@RestController
@RequestMapping("/api/algorithms/dp")
@RequiredArgsConstructor
public class DpController {

    private final DpService dpService;

    @PostMapping("/solve")
    public ResponseEntity<DpResult> solve(@Valid @RequestBody DpSolveRequest req) {
        return ResponseEntity.ok(dpService.compute(
                new DpParams(req.grid(), req.mode())));
    }
}
