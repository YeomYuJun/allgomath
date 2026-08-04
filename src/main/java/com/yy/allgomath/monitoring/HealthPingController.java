package com.yy.allgomath.monitoring;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * actuator 가 9099 로 분리된 뒤에도 8080 의 /actuator/health 를 유지하기 위한 위임 컨트롤러.
 * 도커 healthcheck 와 배포 워크플로가 이 경로를 폴링한다. 상태 판정은 HealthEndpoint 에 위임한다.
 */
@RestController
public class HealthPingController {

    private final HealthEndpoint healthEndpoint;

    public HealthPingController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, String>> health() {
        Status status = healthEndpoint.health().getStatus();
        HttpStatus http = Status.UP.equals(status) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(http).body(Map.of("status", status.getCode()));
    }
}
