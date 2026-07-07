package com.yy.allgomath.sort;

import com.yy.allgomath.sort.dto.SortParams;
import com.yy.allgomath.sort.dto.SortResult;
import com.yy.allgomath.sort.dto.SortRunRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 정렬 트레이스 API. HTTP 변환/검증만, 연산은 {@link SortService}. */
@RestController
@RequestMapping("/api/algorithms/sort")
@RequiredArgsConstructor
public class SortController {

    private final SortService sortService;

    @PostMapping("/run")
    public ResponseEntity<SortResult> run(@Valid @RequestBody SortRunRequest req) {
        return ResponseEntity.ok(sortService.compute(new SortParams(req.algorithm(), req.values())));
    }
}
