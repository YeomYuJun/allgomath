package com.yy.allgomath.telemetry;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 페이지뷰 비컨 수신. 검증과 적재는 TelemetryService. */
@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("/pv")
    public ResponseEntity<Void> pageview(@RequestBody PageviewRequest req) {
        telemetryService.recordPageview(req.route(), req.cid());
        return ResponseEntity.noContent().build();
    }
}
