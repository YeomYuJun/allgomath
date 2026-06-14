package com.yy.allgomath.pendulum;

import com.yy.allgomath.pendulum.dto.PendulumParams;
import com.yy.allgomath.pendulum.dto.PendulumSimulateRequest;
import com.yy.allgomath.pendulum.dto.PendulumState;
import com.yy.allgomath.simulation.SimulationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 이중진자 시뮬레이션 API. HTTP 변환/검증만, 연산은 {@link PendulumService}. */
@RestController
@RequestMapping("/api/algorithms/pendulum")
@RequiredArgsConstructor
public class PendulumController {

    private final PendulumService pendulumService;

    @PostMapping("/simulate")
    public ResponseEntity<SimulationResponse<PendulumState>> simulate(@Valid @RequestBody PendulumSimulateRequest req) {
        PendulumParams params = new PendulumParams(req.state(), req.gravity(), req.armRatio(), req.damping());
        return ResponseEntity.ok(pendulumService.simulate(params, req.steps()));
    }
}
