package com.yy.allgomath.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(

                //일단 fractal 별 다른 TTL 적용
                "mandelbrot", createCacheConfig(Duration.ofHours(2)),    // zoom에 따른 많은 작용이 있을 거 같음
                "julia", createCacheConfig(Duration.ofHours(2)),         // 22
                "fractal", createCacheConfig(Duration.ofHours(1)),       // 통합 캐시

                "fft", createCacheConfig(Duration.ofMinutes(30)),
                "convex", createCacheConfig(Duration.ofMinutes(45))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(createCacheConfig(Duration.ofMinutes(30)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private RedisCacheConfiguration createCacheConfig(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}