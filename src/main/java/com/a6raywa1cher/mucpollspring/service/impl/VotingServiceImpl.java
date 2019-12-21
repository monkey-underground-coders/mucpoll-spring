package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.file.PollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.AnswerAndCountRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class VotingServiceImpl implements VotingService {
	private TemporaryPollSessionRepository temporaryPollSessionRepository;
	private AnswerAndCountRepository answerAndCountRepository;
	private PollSessionRepository pollSessionRepository;
	private UserRepository userRepository;

	@Autowired
	public VotingServiceImpl(TemporaryPollSessionRepository temporaryPollSessionRepository,
	                         AnswerAndCountRepository answerAndCountRepository,
	                         PollSessionRepository pollSessionRepository,
	                         UserRepository userRepository) {
		this.temporaryPollSessionRepository = temporaryPollSessionRepository;
		this.answerAndCountRepository = answerAndCountRepository;
		this.pollSessionRepository = pollSessionRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	@SneakyThrows
	public TemporaryPollSession createNewTemporaryPollSession(Poll poll, long uid) {
		if (poll.getQuestions().size() == 0) {
			return null;
		}
		TemporaryPollSession temporaryPollSession = new TemporaryPollSession();
		temporaryPollSession.setPid(poll.getId());
		temporaryPollSession.setUid(uid);
		temporaryPollSession.setCurrentQid(poll.getQuestions().stream()
				.min(Comparator.comparingInt(PollQuestion::getIndex))
				.get().getId());
		temporaryPollSession.setCreatedAt(LocalDateTime.now());
		temporaryPollSession.serializePoll(poll);
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
	public TemporaryPollSession changeQuestion(String sid, Long qid) throws TemporaryPollSessionNotFound, QuestionNotFoundException {
		Optional<TemporaryPollSession> optional = temporaryPollSessionRepository.findById(sid);
		if (optional.isEmpty()) {
			throw new TemporaryPollSessionNotFound(sid);
		}
		TemporaryPollSession tps = optional.get();
		tps.getQuestions().stream()
				.filter(q -> q.getQid() == qid)
				.findAny()
				.orElseThrow(() -> new QuestionNotFoundException(qid));
		tps.setCurrentQid(qid);
		return temporaryPollSessionRepository.save(tps);
	}

	@Override
	public Optional<TemporaryPollSession> getBySid(String sid) {
		return temporaryPollSessionRepository.findById(sid);
	}

	@Override
	@Transactional
	public PollSession closeVote(String sid) throws TemporaryPollSessionNotFound {
		Optional<TemporaryPollSession> optional = temporaryPollSessionRepository.findById(sid);
		if (optional.isEmpty()) {
			throw new TemporaryPollSessionNotFound(sid);
		}
		return closeVote(optional.get());
	}

	@Override
	@Transactional
	@SneakyThrows
	public PollSession closeVote(TemporaryPollSession tps) {
		Poll poll = tps.deserializePoll();
		PollSession pollSession = pollSessionRepository.save(new PollSession(tps, poll));
		temporaryPollSessionRepository.delete(tps);
		return pollSession;
	}

	@Override
	@Transactional
	public List<PollSession> closeAllVotesByUser(String username) {
		User user = userRepository.getByUsername(username).orElseThrow();
		return temporaryPollSessionRepository.getAllByUid(user.getId()).stream()
				.map(this::closeVote)
				.collect(Collectors.toList());
	}
}
