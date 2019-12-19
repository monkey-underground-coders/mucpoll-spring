package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class AnswerAndCountRepositoryImpl implements AnswerAndCountRepository {
	private static final String KEY = "AnswerAndCount";
	private static Logger logger = LoggerFactory.getLogger(AnswerAndCountRepositoryImpl.class);
	private RedisTemplate redisTemplate;

	public AnswerAndCountRepositoryImpl(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public AnswerAndCount save(AnswerAndCount c) {
		try {
			if (c.getId() == null) {
				c.setId(UUID.randomUUID().toString());
			}
			Map ruleHash = new ObjectMapper().convertValue(c, Map.class);
			redisTemplate.opsForHash().put(KEY, c.getId(), ruleHash);
			return c;
		} catch (Exception e) {
			logger.error("Save to Redis exception", e);
			throw e;
		}
	}

	@Override
	public AnswerAndCount incr(AnswerAndCount c) {
		try {
			redisTemplate.opsForHash().increment(KEY, c.getAid(), 1);
			return c;
		} catch (Exception e) {
			logger.error("Increment exception", e);
			throw e;
		}
	}
}
