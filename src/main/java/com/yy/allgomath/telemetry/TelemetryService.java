package com.yy.allgomath.telemetry;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/** 페이지뷰 카운터와 일자별 UV(HyperLogLog) 적재. */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Pattern UUID_FORM =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Duration UV_TTL = Duration.ofDays(35);

    private final MeterRegistry meterRegistry;
    private final StringRedisTemplate redis;

    public void recordPageview(String route, String cid) {
        if (!KnownRoutes.contains(route)) {
            meterRegistry.counter("site.pageview.rejected").increment();
            return;
        }
        meterRegistry.counter("site.pageview", "route", route).increment();

        if (cid == null || !UUID_FORM.matcher(cid).matches()) {
            return;
        }
        try {
            String key = uvKey(LocalDate.now(SEOUL));
            redis.opsForHyperLogLog().add(key, cid);
            redis.expire(key, UV_TTL);
        } catch (RuntimeException e) {
            log.warn("UV 적재 실패, 카운터는 정상: {}", e.getMessage());
        }
    }

    /** 오늘의 순 방문자 수. Redis 장애 시 -1. */
    public long uvToday() {
        try {
            return redis.opsForHyperLogLog().size(uvKey(LocalDate.now(SEOUL)));
        } catch (RuntimeException e) {
            return -1L;
        }
    }

    static String uvKey(LocalDate date) {
        return "uv:" + date.format(DAY);
    }
}
