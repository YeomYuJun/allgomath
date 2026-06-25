package com.yy.allgomath.bfs;

import com.yy.allgomath.bfs.dto.BfsParams;
import com.yy.allgomath.bfs.dto.BfsResult;
import com.yy.allgomath.bfs.dto.BfsSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 격자 미로 BFS API. HTTP 변환/검증만, 연산은 {@link BfsService}. */
@RestController
@RequestMapping("/api/algorithms/bfs")
@RequiredArgsConstructor
public class BfsController {

    private final BfsService bfsService;

    @PostMapping("/search")
    public ResponseEntity<BfsResult> search(@Valid @RequestBody BfsSearchRequest req) {
        return ResponseEntity.ok(bfsService.compute(
                new BfsParams(req.rows(), req.cols(), req.walls(), req.start(), req.goal(), req.diag())));
    }
}
