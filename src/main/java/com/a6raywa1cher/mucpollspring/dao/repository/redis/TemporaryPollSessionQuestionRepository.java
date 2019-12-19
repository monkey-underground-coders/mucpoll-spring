package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryPollSessionQuestionRepository extends CrudRepository<TemporaryPollSessionQuestion, String> {
}
