package com.yy.allgomath.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class CacheMetricsConfig {

//    @Bean
//    public CacheMetricsRegistrar cacheMetricsRegistrar(MeterRegistry meterRegistry) {
//        return new CacheMetricsRegistrar(meterRegistry);
//    }
//
//    @EventListener
//    public void onCacheHit(CacheHitEvent event) {
//        Metrics.counter("cache.requests", "result", "hit", "cache", event.getCacheName())
//                .increment();
//    }
//
//    @EventListener
//    public void onCacheMiss(CacheMissEvent event) {
//        Metrics.counter("cache.requests", "result", "miss", "cache", event.getCacheName())
//                .increment();
//    }
}