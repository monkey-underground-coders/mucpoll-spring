package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;

public interface VotingService {
	TemporaryPollSession createNewTemporaryPollSession(Long pid);
}
