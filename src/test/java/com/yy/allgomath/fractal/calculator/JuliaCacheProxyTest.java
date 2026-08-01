package com.yy.allgomath.fractal.calculator;

import com.yy.allgomath.fractal.dto.FractalParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JuliaCacheProxyTest.Config.class)
class JuliaCacheProxyTest {

    @Configuration
    @EnableCaching(proxyTargetClass = true) // Spring Boot 기본과 동일: CGLIB 프록시여야 self 주입이 JuliaCalculator 타입으로 해소됨
    static class Config {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("julia");
        }

        @Bean
        JuliaCalculator juliaCalculator(ObjectProvider<JuliaCalculator> self) {
            return new JuliaCalculator(self);
        }
    }

    @Autowired
    JuliaCalculator julia;

    @Autowired
    CacheManager cacheManager;

    @Test
    void calculateWithCaching_routesThroughCacheProxy() {
        FractalParameters params = FractalParameters.juliaDefaults()
                .width(16).height(16).maxIterations(10)
                .cReal(-0.8).cImag(0.156)
                .build();

        julia.calculateWithCaching(params);

        int key = Objects.hash(params.getXMin(), params.getXMax(), params.getYMin(), params.getYMax(),
                params.getWidth(), params.getHeight(), params.getMaxIterations(),
                params.getCReal(), params.getCImag());
        assertThat(cacheManager.getCache("julia").get(key)).isNotNull();
    }
}
