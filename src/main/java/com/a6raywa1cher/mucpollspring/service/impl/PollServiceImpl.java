package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.file.PollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionAnswerRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollServiceImpl implements PollService {
	private UserRepository userRepository;
	private PollRepository pollRepository;
	private PollQuestionRepository pollQuestionRepository;
	private PollQuestionAnswerRepository pollQuestionAnswerRepository;
	private PollSessionRepository pollSessionRepository;

	@Autowired
	public PollServiceImpl(UserRepository userRepository, PollRepository pollRepository,
	                       PollQuestionRepository pollQuestionRepository,
	                       PollQuestionAnswerRepository pollQuestionAnswerRepository,
	                       PollSessionRepository pollSessionRepository) {
		this.userRepository = userRepository;
		this.pollRepository = pollRepository;
		this.pollQuestionRepository = pollQuestionRepository;
		this.pollQuestionAnswerRepository = pollQuestionAnswerRepository;
		this.pollSessionRepository = pollSessionRepository;
	}

	@Override
	public Poll createNewPoll(Long userId, String name) throws UserNotFoundException {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new UserNotFoundException(userId);
		}
		Poll poll = new Poll();
		poll.setName(name);
		poll.setQuestions(Collections.emptyList());
		poll.setCreator(optionalUser.get());
		return pollRepository.save(poll);
	}

	@Override
	public List<Poll> getPollsByUser(Long userId, Pageable pageable) {
		return pollRepository.getAllByCreatorId(userId, pageable);
	}

	@Override
	public Optional<Poll> getById(Long id) {
		return pollRepository.findById(id);
	}

	@Override
	public Poll editPoll(Poll poll, String name) {
		poll.setName(name);
		return pollRepository.save(poll);
	}

	@Override
	@Transactional
	public void deletePoll(Poll poll) {
		long pid = poll.getId();
		pollRepository.delete(poll);
		pollSessionRepository.deleteAllByPid(pid);
	}

	@Override
	@Transactional
	public PollQuestion addQuestion(Poll poll, String title, List<String> answers) {
		PollQuestion pollQuestion = new PollQuestion();
		pollQuestion.setPoll(poll);
		pollQuestion.setQuestion(title);
		pollQuestion.setIndex(pollQuestionRepository.getMaxIndex(poll).orElse(-1) + 1);
		listToPQA(answers, pollQuestion);
		PollQuestion pollQuestion1 = pollQuestionRepository.save(pollQuestion);
		pollRepository.save(poll);
		return pollQuestion1;
	}

	private void listToPQA(List<String> answers, PollQuestion pollQuestion) {
		pollQuestion.setAnswerOptions(new ArrayList<>(answers.size()));
		for (int i = 0; i < answers.size(); i++) {
			PollQuestionAnswer answer = new PollQuestionAnswer();
			answer.setIndex(i);
			answer.setPollQuestion(pollQuestion);
			answer.setAnswer(answers.get(i));
			pollQuestion.getAnswerOptions().add(answer);
		}
	}

	private void normalizeIndexes(List<PollQuestion> list) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setIndex(i);
		}
	}

	@Override
	@Transactional(rollbackOn = HibernateException.class)
	public PollQuestion editQuestion(PollQuestion pollQuestion, String title, Integer index, List<String> answers) {
		pollQuestion.setQuestion(title);

		Poll poll = pollQuestion.getPoll();
		List<PollQuestion> list = poll.getQuestions().stream()
				.sorted(Comparator.comparingInt(PollQuestion::getIndex))
				.collect(Collectors.toCollection(ArrayList::new));
		if (!index.equals(pollQuestion.getIndex())) {
			list.set(list.indexOf(pollQuestion), null);
			list.add(index > pollQuestion.getIndex() ? index + 1 : index, pollQuestion);
			list.remove(null);
			normalizeIndexes(list);
		}
		pollQuestionAnswerRepository.deleteAll(pollQuestion.getAnswerOptions());
		listToPQA(answers, pollQuestion);
		poll.setQuestions(list);
		for (PollQuestion pq : pollQuestionRepository.saveAll(list)) {
			if (pq.getId().equals(pollQuestion.getId())) {
				return pq;
			}
		}
		throw new RuntimeException();
	}

	@Override
	public void deleteQuestion(PollQuestion pollQuestion) {
		List<PollQuestion> list = pollQuestion.getPoll().getQuestions();
		list.remove((int) pollQuestion.getIndex());
		normalizeIndexes(list);
		pollQuestionRepository.saveAll(list);
		pollQuestionRepository.delete(pollQuestion);
	}

	@Override
	public Page<PollSession> getPollSessionsPage(Long pid, Pageable pageable) {
		return pollSessionRepository.getPageByPid(pid, pageable);
	}

	@Override
	public Optional<PollSession> getPollSession(Long pid, String sid) {
		return pollSessionRepository.getBySidAndPid(pid, sid);
	}

	@Override
	public void deletePollSession(Long pid, String sid) {
		pollSessionRepository.delete(pid, sid);
	}
}
