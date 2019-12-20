package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnswerAndCountRepositoryImpl implements AnswerAndCountRepository {
	private static Logger logger = LoggerFactory.getLogger(AnswerAndCountRepositoryImpl.class);
	private RedisTemplate redisTemplate;

	public AnswerAndCountRepositoryImpl(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public AnswerAndCount incr(TemporaryPollSession temporaryPollSession, AnswerAndCount c) {
		try {
			String id = temporaryPollSession.getId();
			int questionIndex = 0;
			Integer answerIndex = null;
			List<TemporaryPollSessionQuestion> questions = temporaryPollSession.getQuestions();
			for (; questionIndex < questions.size(); questionIndex++) {
				TemporaryPollSessionQuestion q = questions.get(questionIndex);
				if (q.getMap().contains(c)) {
					answerIndex = q.getMap().indexOf(c);
					break;
				}
			}
			if (answerIndex == null) {
				throw new NullPointerException();
			}
			long newVal = redisTemplate.opsForHash().increment(String.format("PollSession:%s", id),
					String.format("questions.[%d].map.[%d].count", questionIndex, answerIndex), 1);
			c.setCount(newVal);
			return c;
		} catch (Exception e) {
			logger.error("Increment exception", e);
			throw e;
		}
	}
}
