package com.yy.allgomath.controller;

import com.yy.allgomath.datatype.MonteCarloRequest;
import com.yy.allgomath.datatype.MonteCarloResult;
import com.yy.allgomath.service.MonteCarloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monte-carlo")
public class MonteCarloController {

    @Autowired
    private MonteCarloService monteCarloService;

    @PostMapping("/integrate")
    public ResponseEntity<MonteCarloResult> integrate(@RequestBody MonteCarloRequest request) {
        MonteCarloResult result = monteCarloService.performMonteCarloIntegration(request);
        return ResponseEntity.ok(result);
    }
}