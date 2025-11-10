package com.ftp.authservice.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Simple in-memory cache for dev. Replace with RedisCacheManager in prod.
 * TTL can be enforced via Redis configuration; left simple here for clarity.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        // For production: return RedisCacheManager with TTL = cache.permissions-ttl-seconds
        return new ConcurrentMapCacheManager("permissions");
    }
}
