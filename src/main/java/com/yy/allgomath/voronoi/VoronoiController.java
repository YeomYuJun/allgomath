package com.yy.allgomath.voronoi;

import com.yy.allgomath.voronoi.dto.VoronoiComputeRequest;
import com.yy.allgomath.voronoi.dto.VoronoiParams;
import com.yy.allgomath.voronoi.dto.VoronoiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Voronoi 다이어그램 API. HTTP 변환/검증만, 연산은 {@link VoronoiService}. */
@RestController
@RequestMapping("/api/algorithms/voronoi")
@RequiredArgsConstructor
public class VoronoiController {

    private final VoronoiService voronoiService;

    @PostMapping("/compute")
    public ResponseEntity<VoronoiResult> compute(@Valid @RequestBody VoronoiComputeRequest req) {
        return ResponseEntity.ok(voronoiService.compute(new VoronoiParams(req.sites(), req.metric(), req.grid())));
    }
}
