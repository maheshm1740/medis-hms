package com.medis.hospital_system.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableCaching
@Profile("test")
public class EmbeddedRedisConfig {

    @Bean
    @Primary  // ← forces this CacheManager to win over auto-configured RedisCacheManager
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary  // ← add this
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
