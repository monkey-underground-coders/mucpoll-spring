package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.file.PollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.AnswerAndCountRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class VotingServiceImpl implements VotingService {
	private TemporaryPollSessionRepository temporaryPollSessionRepository;
	private AnswerAndCountRepository answerAndCountRepository;
	private PollSessionRepository pollSessionRepository;
	private PollRepository pollRepository;

	@Autowired
	public VotingServiceImpl(TemporaryPollSessionRepository temporaryPollSessionRepository,
	                         AnswerAndCountRepository answerAndCountRepository,
	                         PollSessionRepository pollSessionRepository,
	                         PollRepository pollRepository) {
		this.temporaryPollSessionRepository = temporaryPollSessionRepository;
		this.answerAndCountRepository = answerAndCountRepository;
		this.pollSessionRepository = pollSessionRepository;
		this.pollRepository = pollRepository;
	}

	@Override
	@Transactional
	public TemporaryPollSession createNewTemporaryPollSession(Poll poll, long uid, String simpSessionId) {
		if (poll.getQuestions().size() == 0) {
			throw new RuntimeException("Empty poll");
		}
		TemporaryPollSession temporaryPollSession = new TemporaryPollSession();
		temporaryPollSession.setPid(poll.getId());
		temporaryPollSession.setUid(uid);
		temporaryPollSession.setCurrentQid(poll.getQuestions().stream()
				.min(Comparator.comparingInt(PollQuestion::getIndex))
				.get().getId());
		temporaryPollSession.setCreatedAt(LocalDateTime.now());
		temporaryPollSession.setSimpSessionId(simpSessionId);
		try {
			temporaryPollSession.serializePoll(poll);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	public TemporaryPollSession start(String sid) throws TemporaryPollSessionNotFound {
		Optional<TemporaryPollSession> temporaryPollSession = temporaryPollSessionRepository.findById(sid);
		if (temporaryPollSession.isEmpty()) {
			throw new TemporaryPollSessionNotFound(sid);
		}
		TemporaryPollSession tps = temporaryPollSession.get();
		tps.setStarted(true);
		return temporaryPollSessionRepository.save(tps);
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
		boolean isAnyVoteDetected = tps.getQuestions().stream()
				.flatMap(tpsq -> tpsq.getMap().stream())
				.map(AnswerAndCount::getCount)
				.anyMatch(l -> l > 0);
		if (isAnyVoteDetected) {
			try {
				PollSession pollSession = pollSessionRepository.save(new PollSession(tps, poll));
				pollRepository.incrementLaunchedCount(poll, 1);
				temporaryPollSessionRepository.delete(tps);
				return pollSession;
			} catch (NullPointerException npe) {
				ObjectMapper objectMapper = new ObjectMapper()
						.registerModule(new Jdk8Module())
						.registerModule(new JavaTimeModule())
						.enable(SerializationFeature.INDENT_OUTPUT);
				log.error("Error while saving PollSession. tps:" + objectMapper.writeValueAsString(tps) + "\n"
						+ "poll:" + (poll == null ? "null" : objectMapper.writeValueAsString(poll)), npe);
				return null;
			}
		} else {
			temporaryPollSessionRepository.delete(tps);
			return null;
		}
	}

	@Override
	public List<PollSession> closeAllVotesBySimpSessionId(String simpSessionId) {
		List<TemporaryPollSession> bySimpSessionId = temporaryPollSessionRepository.getAllBySimpSessionId(simpSessionId);
		return bySimpSessionId.stream()
				.map(this::closeVote)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
