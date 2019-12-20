package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.redis.AnswerAndCountRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionRepository;
import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

//import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionQuestionRepository;

@Service
public class VotingServiceImpl implements VotingService {
	private TemporaryPollSessionRepository temporaryPollSessionRepository;
	//	private TemporaryPollSessionQuestionRepository temporaryPollSessionQuestionRepository;
	private AnswerAndCountRepository answerAndCountRepository;

	@Autowired
	public VotingServiceImpl(TemporaryPollSessionRepository temporaryPollSessionRepository,
//	                         TemporaryPollSessionQuestionRepository temporaryPollSessionQuestionRepository,
                             AnswerAndCountRepository answerAndCountRepository) {
		this.temporaryPollSessionRepository = temporaryPollSessionRepository;
//		this.temporaryPollSessionQuestionRepository = temporaryPollSessionQuestionRepository;
		this.answerAndCountRepository = answerAndCountRepository;
	}

	@Override
	public TemporaryPollSession createNewTemporaryPollSession(Poll poll) {
		if (poll.getQuestions().size() == 0) {
			return null;
		}
		TemporaryPollSession temporaryPollSession = new TemporaryPollSession();
		temporaryPollSession.setPid(poll.getId());
		temporaryPollSession.setCurrentQid(poll.getQuestions().stream()
				.min(Comparator.comparingInt(PollQuestion::getIndex))
				.get().getId());
		temporaryPollSession.setQuestions(poll.getQuestions().stream()
				.sorted(Comparator.comparingInt(PollQuestion::getIndex))
				.map(pq -> {
					TemporaryPollSessionQuestion question = new TemporaryPollSessionQuestion();
					question.setQid(pq.getId());
					question.setMap(pq.getAnswerOptions().stream()
							.sorted(Comparator.comparingInt(PollQuestionAnswer::getIndex))
							.map(answer -> {
								AnswerAndCount answerAndCount = new AnswerAndCount();
								answerAndCount.setAid(answer.getId());
								answerAndCount.setCount(0);
								return answerAndCount;
							})
							.collect(Collectors.toList()));
					return question;
				})
				.collect(Collectors.toList()));
		TemporaryPollSession save = temporaryPollSessionRepository.save(temporaryPollSession);
		return save;
	}

	@Override
	public AnswerAndCount appendVote(String sid, Long aid) throws TemporaryPollSessionNotFound, AnswerNotFoundException {
		Optional<TemporaryPollSession> temporaryPollSession = temporaryPollSessionRepository.findById(sid);
		if (temporaryPollSession.isEmpty()) {
			throw new TemporaryPollSessionNotFound(sid);
		}
		Optional<AnswerAndCount> answerAndCount = temporaryPollSession.stream()
				.flatMap(s -> s.getQuestions().stream())
				.flatMap(q -> q.getMap().stream())
				.filter(a -> a.getAid() == aid)
				.findFirst();
		if (answerAndCount.isEmpty()) {
			throw new AnswerNotFoundException(aid);
		}
		return answerAndCountRepository.incr(temporaryPollSession.get(), answerAndCount.get());
	}

	@Override
	public Optional<TemporaryPollSession> getBySid(String sid) {
		return temporaryPollSessionRepository.findById(sid);
	}
}
