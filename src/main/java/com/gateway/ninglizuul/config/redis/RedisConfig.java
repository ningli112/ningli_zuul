package com.gateway.ninglizuul.config.redis;

import java.io.Serializable;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 
 * @ClassName: RedisConfig
 * @Description:redis 自定义配置
 * @author: 宁黎
 * @date: 2019年5月9日 下午3:49:51
 */
@Configuration
public class RedisConfig {
	/**
	 * 
	 * @Title: redisTemplate
	 * @Description: redis序列化
	 * @param redisConnectionFactory
	 * @return
	 * 		RedisTemplate<String,Serializable>
	 * @author: 宁黎
	 * @date:2019年5月7日下午7:31:58
	 *
	 */
	@Bean
	public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Serializable> template = new RedisTemplate<String, Serializable>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setConnectionFactory(connectionFactory);
		return template;
	}

	/**
	 * 
	 * @Title: cacheManager
	 * @Description: 自定义缓存管理，定义缓存有效时间
	 * @param redisConnectionFactory
	 * @return
	 * 		CacheManager
	 * @author: 宁黎
	 * @date:2019年5月7日下午7:48:56
	 *
	 */
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, Serializable> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setDefaultExpiration(3600);
		return cacheManager;
	}
}
