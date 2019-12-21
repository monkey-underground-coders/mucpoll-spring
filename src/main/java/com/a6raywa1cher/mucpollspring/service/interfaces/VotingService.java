package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface VotingService {
	TemporaryPollSession createNewTemporaryPollSession(Poll poll, long uid);

	AnswerAndCount appendVote(String sid, Long aid) throws TemporaryPollSessionNotFound, AnswerNotFoundException;

	TemporaryPollSession changeQuestion(String sid, Long qid) throws TemporaryPollSessionNotFound, QuestionNotFoundException;

	Optional<TemporaryPollSession> getBySid(String sid);

	PollSession closeVote(String sid) throws TemporaryPollSessionNotFound;

	@Transactional
	PollSession closeVote(TemporaryPollSession tps);

	List<PollSession> closeAllVotesByUser(String username);
}
