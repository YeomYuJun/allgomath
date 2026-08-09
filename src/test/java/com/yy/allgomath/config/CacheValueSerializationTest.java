package com.yy.allgomath.config;

import com.yy.allgomath.fourier.dto.FourierResult;
import com.yy.allgomath.fourier.dto.Harmonic;
import com.yy.allgomath.plotter.dto.GradPoint;
import com.yy.allgomath.plotter.dto.SurfaceResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CacheValueSerializationTest {

    @Test
    @DisplayName("double[][] 캐시 값이 왕복한다")
    void doubleMatrixRoundTrips() {
        RedisSerializer<double[][]> serializer = CacheConfig.typedSerializer(double[][].class);
        double[][] value = {{0.5, 1.5}, {2.5, 3.5}};

        double[][] restored = serializer.deserialize(serializer.serialize(value));

        assertThat(restored).isDeepEqualTo(value);
    }

    @Test
    @DisplayName("타입 정보 없이 저장된 기존 double[][] 항목도 읽힌다")
    void legacyUntypedMatrixIsReadable() {
        RedisSerializer<double[][]> serializer = CacheConfig.typedSerializer(double[][].class);
        byte[] legacy = "[[0.5,1.5],[2.5,3.5]]".getBytes(StandardCharsets.UTF_8);

        double[][] restored = serializer.deserialize(legacy);

        assertThat(restored).isDeepEqualTo(new double[][]{{0.5, 1.5}, {2.5, 3.5}});
    }

    @Test
    @DisplayName("record 캐시 값이 왕복한다")
    void recordValuesRoundTrip() {
        RedisSerializer<SurfaceResult> surfaceSerializer = CacheConfig.typedSerializer(SurfaceResult.class);
        SurfaceResult surface = new SurfaceResult(new double[][]{{1.0, 2.0}}, 1.0, 2.0, new GradPoint(3, 0.1, 0.2, 0.3, 0.4, 0.5));

        SurfaceResult restoredSurface = surfaceSerializer.deserialize(surfaceSerializer.serialize(surface));

        assertThat(restoredSurface.z()).isDeepEqualTo(surface.z());
        assertThat(restoredSurface.zMin()).isEqualTo(1.0);
        assertThat(restoredSurface.zMax()).isEqualTo(2.0);
        assertThat(restoredSurface.critical()).isEqualTo(surface.critical());

        RedisSerializer<FourierResult> fourierSerializer = CacheConfig.typedSerializer(FourierResult.class);
        FourierResult fourier = new FourierResult(List.of(new Harmonic(1, 0.5), new Harmonic(3, 0.25)));

        FourierResult restoredFourier = fourierSerializer.deserialize(fourierSerializer.serialize(fourier));

        assertThat(restoredFourier.harmonics()).containsExactly(new Harmonic(1, 0.5), new Harmonic(3, 0.25));
    }
}
