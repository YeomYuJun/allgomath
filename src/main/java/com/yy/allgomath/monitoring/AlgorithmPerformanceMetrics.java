package com.yy.allgomath.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmPerformanceMetrics {
    private final MeterRegistry meterRegistry;

    private Timer fractalTimer;
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;

    // 생성자 주입으로 meterRegistry 초기화
    public AlgorithmPerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        this.fractalTimer = Timer.builder("fractal.generation.time")
                .description("프랙탈 생성 시간")
                .register(meterRegistry);

        this.cacheHitCounter = Counter.builder("cache.requests")
                .tag("result", "hit")
                .description("캐시 히트 수")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("cache.requests")
                .tag("result", "miss")
                .description("캐시 미스 수")
                .register(meterRegistry);
    }

    // 메트릭 사용을 위한 메서드들
    public Timer.Sample startFractalTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordFractalTime(Timer.Sample sample, String fractalType) {
        sample.stop(Timer.builder("fractal.generation.time")
                .tag("type", fractalType)
                .register(meterRegistry));
    }

    public void recordCacheHit() {
        cacheHitCounter.increment();
    }

    public void recordCacheMiss() {
        cacheMissCounter.increment();
    }
}