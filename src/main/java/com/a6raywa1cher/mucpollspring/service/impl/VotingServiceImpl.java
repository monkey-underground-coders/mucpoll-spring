package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionQuestionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.redis.TemporaryPollSessionRepository;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class VotingServiceImpl implements VotingService {
	private TemporaryPollSessionRepository temporaryPollSessionRepository;
	private TemporaryPollSessionQuestionRepository temporaryPollSessionQuestionRepository;

	@Autowired
	public VotingServiceImpl(TemporaryPollSessionRepository temporaryPollSessionRepository,
	                         TemporaryPollSessionQuestionRepository temporaryPollSessionQuestionRepository) {
		this.temporaryPollSessionRepository = temporaryPollSessionRepository;
		this.temporaryPollSessionQuestionRepository = temporaryPollSessionQuestionRepository;
	}

	@Override
	public TemporaryPollSession createNewTemporaryPollSession(Long pid) {
		TemporaryPollSession temporaryPollSession = new TemporaryPollSession();
		temporaryPollSession.setPid(pid);
		temporaryPollSession.setQuestions(Collections.emptyList());
		return temporaryPollSessionRepository.save(temporaryPollSession);
	}
}
