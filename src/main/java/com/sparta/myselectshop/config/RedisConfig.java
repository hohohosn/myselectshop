package com.sparta.myselectshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

// redis 적용
@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
		ObjectMapper objectMapper) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// Key Serializer
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());

		// Value Serializer
		GenericJackson2JsonRedisSerializer serializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
		ObjectMapper objectMapper) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(30)) // 기본 TTL 30분
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
			)
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer(objectMapper)
				)
			);

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(config)
			.withCacheConfiguration("products",
				config.entryTtl(Duration.ofMinutes(60))) // 상품 캐시 1시간
			.withCacheConfiguration("popularProducts",
				config.entryTtl(Duration.ofMinutes(10))) // 인기 상품 10분
			.withCacheConfiguration("productDetail",
				config.entryTtl(Duration.ofHours(2))) // 상품 상세 2시간
			.build();
	}
}