package com.ieji.rpg.infra.redis;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;

import java.time.Duration;
/// Bean de configuração de cache para o redis
/// Define o tempo de cache maximo para uma nova requisição no banco como 10 minutos
/// Impede valores nulos no cache
/// define o método de serialização do cache
///
@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder.cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(GenericJacksonJsonRedisSerializer.builder().build())
                        )
        );
    }
}