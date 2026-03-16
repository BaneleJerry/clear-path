package com.banelethabede.clear_path_parent.security.config;

import org.springframework.boot.actuate.metrics.cache.RedisCacheMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.data.;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }
}

