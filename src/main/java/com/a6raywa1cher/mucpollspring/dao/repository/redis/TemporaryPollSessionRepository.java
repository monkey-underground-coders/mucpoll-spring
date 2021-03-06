package com.a6raywa1cher.mucpollspring.dao.repository.redis;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemporaryPollSessionRepository extends CrudRepository<TemporaryPollSession, String> {
	List<TemporaryPollSession> getAllByUid(long uid);

	List<TemporaryPollSession> getAllBySimpSessionId(String simpSessionId);
}
