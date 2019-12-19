package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;

import java.util.Optional;

public interface VotingService {
	TemporaryPollSession createNewTemporaryPollSession(Poll poll);

	Optional<TemporaryPollSession> getBySid(String sid);
}
