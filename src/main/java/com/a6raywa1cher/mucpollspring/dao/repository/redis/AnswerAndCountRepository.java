package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;

public interface AnswerAndCountRepository {
	AnswerAndCount save(AnswerAndCount c);

	AnswerAndCount incr(AnswerAndCount c);
}
