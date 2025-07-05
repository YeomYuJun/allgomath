package com.yy.allgomath.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

        //직렬화 이슈로 인한 추가사항
        // 커스텀 ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // ✅ 핵심: 타입 정보 보존을 위한 설정 추가
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 다차원 배열 직렬화 지원
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);




        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(

                //일단 fractal 별 다른 TTL 적용
                "mandelbrot", createCacheConfig(Duration.ofHours(2), serializer),    // zoom에 따른 많은 작용이 있을 거 같음
                "julia", createCacheConfig(Duration.ofHours(2), serializer),         // 22
                //"fractal", createCacheConfig(Duration.ofHours(1), serializer),       // 통합 캐시 -> 안쓸 거 같음
                "mandelbrot_tile", createCacheConfig(Duration.ofHours(3), serializer),

                "fft", createCacheConfig(Duration.ofMinutes(30), serializer),
                "convex", createCacheConfig(Duration.ofMinutes(45), serializer)
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(createCacheConfig(Duration.ofMinutes(30), serializer))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private RedisCacheConfiguration createCacheConfig(Duration ttl,
                                                      GenericJackson2JsonRedisSerializer serializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));
    }
}