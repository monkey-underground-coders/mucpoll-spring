package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;

public interface AnswerAndCountRepository {
	AnswerAndCount incr(TemporaryPollSession temporaryPollSession, AnswerAndCount c);
}
