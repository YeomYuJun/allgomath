package com.yy.allgomath.flow;

import com.yy.allgomath.flow.dto.FlowParams;
import com.yy.allgomath.flow.dto.FlowSimulateRequest;
import com.yy.allgomath.simulation.SimulationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Perlin flow field 시뮬레이션 API. HTTP 변환/검증만, 연산은 {@link FlowService}. */
@RestController
@RequestMapping("/api/algorithms/flow")
@RequiredArgsConstructor
public class FlowController {

    private final FlowService flowService;

    @PostMapping("/simulate")
    public ResponseEntity<SimulationResponse<double[][]>> simulate(@Valid @RequestBody FlowSimulateRequest req) {
        return ResponseEntity.ok(flowService.simulate(new FlowParams(req.particles(), req.scale(), req.time()), req.steps()));
    }
}
