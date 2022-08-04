package com.kdt.team04.common.redis;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setDataWithExpiration(String key, String value, long time) {
		this.getData(key).ifPresent(data ->
			this.deleteData(key)
		);
		Duration expireDuration = Duration.ofSeconds(time);
		redisTemplate.opsForValue().set(key, value, expireDuration);
	}

	public Optional<String> getData(String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
}
