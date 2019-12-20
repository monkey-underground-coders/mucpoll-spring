package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;

import java.util.Optional;

public interface VotingService {
	TemporaryPollSession createNewTemporaryPollSession(Poll poll);

	AnswerAndCount appendVote(String sid, Long aid) throws TemporaryPollSessionNotFound, AnswerNotFoundException;

	Optional<TemporaryPollSession> getBySid(String sid);
}
