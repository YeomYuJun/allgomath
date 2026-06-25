package com.yy.allgomath.greedy;

import com.yy.allgomath.greedy.dto.GreedyParams;
import com.yy.allgomath.greedy.dto.GreedyResult;
import com.yy.allgomath.greedy.dto.GreedyScheduleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 인터벌 스케줄링 그리디 API. HTTP 변환/검증만, 연산은 {@link GreedyService}. */
@RestController
@RequestMapping("/api/algorithms/greedy")
@RequiredArgsConstructor
public class GreedyController {

    private final GreedyService greedyService;

    @PostMapping("/schedule")
    public ResponseEntity<GreedyResult> schedule(@Valid @RequestBody GreedyScheduleRequest req) {
        return ResponseEntity.ok(greedyService.compute(
                new GreedyParams(req.tasks(), req.strategy())));
    }
}
