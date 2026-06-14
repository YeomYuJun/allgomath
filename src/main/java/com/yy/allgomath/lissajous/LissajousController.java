package com.yy.allgomath.lissajous;

import com.yy.allgomath.lissajous.dto.LissajousParams;
import com.yy.allgomath.lissajous.dto.LissajousSimulateRequest;
import com.yy.allgomath.lissajous.dto.Point;
import com.yy.allgomath.simulation.SimulationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 리사주 시뮬레이션 API. HTTP 변환/검증만, 연산은 {@link LissajousService}. */
@RestController
@RequestMapping("/api/algorithms/lissajous")
@RequiredArgsConstructor
public class LissajousController {

    private final LissajousService lissajousService;

    @PostMapping("/simulate")
    public ResponseEntity<SimulationResponse<Point>> simulate(@Valid @RequestBody LissajousSimulateRequest req) {
        LissajousParams params = new LissajousParams(req.a(), req.b(), req.delta(), req.phase());
        return ResponseEntity.ok(lissajousService.simulate(params, req.steps()));
    }
}
