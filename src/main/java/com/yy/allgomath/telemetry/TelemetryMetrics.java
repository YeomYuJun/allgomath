package com.yy.allgomath.telemetry;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/** site.uv.today Gauge 등록. 스크레이프마다 PFCOUNT를 때리지 않도록 10초 캐시한다. */
@Component
@RequiredArgsConstructor
public class TelemetryMetrics {

    private static final long CACHE_MILLIS = 10_000L;

    private final MeterRegistry meterRegistry;
    private final TelemetryService telemetryService;

    private final AtomicLong cachedValue = new AtomicLong(0L);
    private final AtomicLong cachedAt = new AtomicLong(0L);

    @PostConstruct
    public void register() {
        Gauge.builder("site.uv.today", this, TelemetryMetrics::currentUv)
                .description("Asia/Seoul 기준 오늘의 순 방문자 근사치")
                .register(meterRegistry);
    }

    private double currentUv() {
        long now = System.currentTimeMillis();
        if (now - cachedAt.get() > CACHE_MILLIS) {
            cachedValue.set(Math.max(0L, telemetryService.uvToday()));
            cachedAt.set(now);
        }
        return cachedValue.get();
    }
}
