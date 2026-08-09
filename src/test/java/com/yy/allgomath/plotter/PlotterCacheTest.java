package com.yy.allgomath.plotter;

import com.yy.allgomath.fourier.FourierService;
import com.yy.allgomath.fourier.dto.FourierParams;
import com.yy.allgomath.fourier.dto.FourierResult;
import com.yy.allgomath.plotter.dto.SurfaceParams;
import com.yy.allgomath.plotter.dto.SurfaceResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PlotterCacheTest.Config.class)
class PlotterCacheTest {

    @Configuration
    @EnableCaching(proxyTargetClass = true) // Spring Boot 기본과 동일: 인터페이스 구현 서비스도 concrete 타입으로 주입되도록 CGLIB 프록시 사용
    static class Config {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("plotter_surface", "fourier_series");
        }

        @Bean
        PlotterService plotterService() {
            return new PlotterService();
        }

        @Bean
        FourierService fourierService() {
            return new FourierService();
        }
    }

    @Autowired
    PlotterService plotter;

    @Autowired
    FourierService fourier;

    @Autowired
    CacheManager cacheManager;

    @Test
    void surface_cachesAndReusesSameResult() {
        SurfaceParams params = new SurfaceParams("saddle", 2.4, 40);

        SurfaceResult first = plotter.surface(params);
        SurfaceResult second = plotter.surface(params);

        assertThat(cacheManager.getCache("plotter_surface").get("saddle_2.4_40")).isNotNull();
        assertThat(second).isSameAs(first);
    }

    @Test
    void fourierSeries_cachesAndReusesSameResult() {
        FourierParams params = new FourierParams("square", 20);

        FourierResult first = fourier.compute(params);
        FourierResult second = fourier.compute(params);

        assertThat(cacheManager.getCache("fourier_series").get("square_20")).isNotNull();
        assertThat(second).isSameAs(first);
    }
}
