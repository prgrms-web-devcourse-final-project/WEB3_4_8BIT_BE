package com.backend.global.config;

import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
@EnableRedisRepositories
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String REDIS_HOST;

	@Value("${spring.data.redis.port}")
	private String REDIS_PORT;

	// @Value("${spring.data.redis.password}")
	// private String REDIS_PASSWORD;

	private static final String REDISSON_HOST_PREFIX = "redis://";

	/**
	 * RedisTemplate 설정
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(
		final RedisConnectionFactory factory,
		final ObjectMapper objectMapper
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		return template;
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
			).disableCachingNullValues();
	}

	@Bean
	public CacheManager cacheManager(
		final RedisConnectionFactory redisConnectionFactory
	) {
		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(cacheConfiguration())
			.build();
	}

	@Bean
	public RedisTemplate<String, List<FishingTripPostResponse.HotPost>> hotPostRedisTemplate(
		RedisConnectionFactory factory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<String, List<FishingTripPostResponse.HotPost>> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		return template;
	}

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress(REDISSON_HOST_PREFIX + REDIS_HOST + ":" + REDIS_PORT);
			// .setPassword(REDIS_PASSWORD);

		return Redisson.create(config);
	}

}