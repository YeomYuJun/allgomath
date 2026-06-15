package com.yy.allgomath.plotter;

import com.yy.allgomath.plotter.dto.DescentParams;
import com.yy.allgomath.plotter.dto.DescentRequest;
import com.yy.allgomath.plotter.dto.DescentResult;
import com.yy.allgomath.plotter.dto.PlotterSurfaceRequest;
import com.yy.allgomath.plotter.dto.SurfaceParams;
import com.yy.allgomath.plotter.dto.SurfaceResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 3D plotter API. HTTP/validation only; computation delegated to {@link PlotterService}. */
@RestController
@RequestMapping("/api/algorithms/plotter")
@RequiredArgsConstructor
public class PlotterController {

    private final PlotterService plotterService;

    @PostMapping("/surface")
    public ResponseEntity<SurfaceResult> surface(@Valid @RequestBody PlotterSurfaceRequest req) {
        return ResponseEntity.ok(plotterService.surface(
                new SurfaceParams(req.fn(), req.range(), req.resolution())));
    }

    @PostMapping("/gradient-descent")
    public ResponseEntity<DescentResult> descend(@Valid @RequestBody DescentRequest req) {
        return ResponseEntity.ok(plotterService.descend(
                new DescentParams(req.fn(), req.startX(), req.startY(), req.learningRate(), req.maxIterations())));
    }
}
