package com.yy.allgomath.automata;

import com.yy.allgomath.automata.dto.LifeSimulateRequest;
import com.yy.allgomath.simulation.SimulationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 셀룰러 오토마타(라이프) 시뮬레이션 API. HTTP 변환/검증만, 연산은 {@link LifeService}. */
@RestController
@RequestMapping("/api/algorithms/automata")
@RequiredArgsConstructor
public class AutomataController {

    private final LifeService lifeService;

    @PostMapping("/life/simulate")
    public ResponseEntity<SimulationResponse<boolean[][]>> simulate(@Valid @RequestBody LifeSimulateRequest req) {
        return ResponseEntity.ok(lifeService.simulate(req.grid(), req.steps()));
    }
}
