package com.medis.hospital_system.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medis.hospital_system.infrastructure.cache.CacheNames;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
@Profile("!test")
public class RedisConfig {

    // Bump this version whenever the serialization format changes.
    // Old keys (v1::, v2::, etc.) are simply never read — no flush needed.
    private static final String CACHE_VERSION = "v3";

    private ObjectMapper buildRedisObjectMapper() {
        var ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.medis.hospital_system")
                .allowIfSubType("java.util.ArrayList")
                .allowIfSubType("java.util.LinkedList")
                .allowIfSubType("java.math.BigDecimal")
                .build();

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .activateDefaultTyping(
                        ptv,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY
                );
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        var config = new RedisStandaloneConfiguration(
                redisProperties.getHost(), redisProperties.getPort());
        return new LettuceConnectionFactory(config);
    }

    private RedisCacheConfiguration base() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                // Versioned prefix — old entries from different versions are ignored, not read
                .computePrefixWith(cacheName -> CACHE_VERSION + "::" + cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(buildRedisObjectMapper())));
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return base().entryTtl(Duration.ofMinutes(10));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .withInitialCacheConfigurations(Map.ofEntries(
                        Map.entry(CacheNames.USERS,                   base().entryTtl(Duration.ofMinutes(30))),
                        Map.entry(CacheNames.DOCTORS,                 base().entryTtl(Duration.ofMinutes(30))),
                        Map.entry(CacheNames.PATIENTS,                base().entryTtl(Duration.ofMinutes(30))),
                        Map.entry(CacheNames.MEDICINES,               base().entryTtl(Duration.ofMinutes(30))),
                        Map.entry(CacheNames.DOCTORS_ALL,             base().entryTtl(Duration.ofMinutes(10))),
                        Map.entry(CacheNames.MEDICINES_ALL,           base().entryTtl(Duration.ofMinutes(10))),
                        Map.entry(CacheNames.DOCTORS_BY_SPEC,         base().entryTtl(Duration.ofMinutes(10))),
                        Map.entry(CacheNames.APPOINTMENTS,            base().entryTtl(Duration.ofMinutes(5))),
                        Map.entry(CacheNames.APPOINTMENTS_BY_DOCTOR,  base().entryTtl(Duration.ofMinutes(5))),
                        Map.entry(CacheNames.APPOINTMENTS_BY_PATIENT, base().entryTtl(Duration.ofMinutes(5))),
                        Map.entry(CacheNames.APPOINTMENTS_BY_STATUS,  base().entryTtl(Duration.ofMinutes(5))),
                        Map.entry(CacheNames.INVENTORY,               base().entryTtl(Duration.ofMinutes(2)))
                ))
                .build();
    }
}