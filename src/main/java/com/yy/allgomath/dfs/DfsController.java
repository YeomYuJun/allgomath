package com.yy.allgomath.dfs;

import com.yy.allgomath.dfs.dto.DfsParams;
import com.yy.allgomath.dfs.dto.DfsResult;
import com.yy.allgomath.dfs.dto.DfsSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 격자 미로 DFS API. HTTP 변환/검증만, 연산은 {@link DfsService}. */
@RestController
@RequestMapping("/api/algorithms/dfs")
@RequiredArgsConstructor
public class DfsController {

    private final DfsService dfsService;

    @PostMapping("/search")
    public ResponseEntity<DfsResult> search(@Valid @RequestBody DfsSearchRequest req) {
        return ResponseEntity.ok(dfsService.compute(
                new DfsParams(req.rows(), req.cols(), req.walls(), req.start(), req.goal())));
    }
}
