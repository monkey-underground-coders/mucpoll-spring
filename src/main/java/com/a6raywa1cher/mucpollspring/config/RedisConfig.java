package com.a6raywa1cher.mucpollspring.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
@EnableRedisRepositories(
		basePackages =
				{
						"com.a6raywa1cher.mucpollspring.models.redis",
						"com.a6raywa1cher.mucpollspring.dao.repository.redis"
				}
)
public class RedisConfig {
	@Bean
	JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisProperties.getHost());
		redisStandaloneConfiguration.setPort(redisProperties.getPort());
		redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
		if (StringUtils.hasLength(redisProperties.getPassword())) {
			redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		}
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	StringRedisSerializer stringRedisSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisProperties redisProperties) {
		final RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory(redisProperties));
		template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
		template.setKeySerializer(stringRedisSerializer());
		template.setHashKeySerializer(stringRedisSerializer());
		return template;
	}
}
