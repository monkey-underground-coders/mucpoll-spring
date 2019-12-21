package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionAnswerRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PollServiceImpl implements PollService {
	private UserRepository userRepository;
	private PollRepository pollRepository;
	private PollQuestionRepository pollQuestionRepository;
	private PollQuestionAnswerRepository pollQuestionAnswerRepository;

	@Autowired
	public PollServiceImpl(UserRepository userRepository, PollRepository pollRepository,
	                       PollQuestionRepository pollQuestionRepository,
	                       PollQuestionAnswerRepository pollQuestionAnswerRepository) {
		this.userRepository = userRepository;
		this.pollRepository = pollRepository;
		this.pollQuestionRepository = pollQuestionRepository;
		this.pollQuestionAnswerRepository = pollQuestionAnswerRepository;
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
	public List<Poll> getPollsByUser(Long userId) {
		return pollRepository.getAllByCreatorId(userId);
	}

	@Override
	public Optional<Poll> getById(Long id) {
		return pollRepository.findById(id);
	}

	@Override
	@Transactional
	public PollQuestion addQuestion(Poll poll, String title, List<String> answers) {
		PollQuestion pollQuestion = new PollQuestion();
		pollQuestion.setPoll(poll);
		pollQuestion.setQuestion(title);
		pollQuestion.setIndex(pollQuestionRepository.getMaxIndex(poll).orElse(-1) + 1);
		pollQuestion.setAnswerOptions(new ArrayList<>(answers.size()));
		for (int i = 0; i < answers.size(); i++) {
			PollQuestionAnswer answer = new PollQuestionAnswer();
			answer.setIndex(i);
			answer.setPollQuestion(pollQuestion);
			answer.setAnswer(answers.get(i));
			pollQuestion.getAnswerOptions().add(answer);
		}
		PollQuestion pollQuestion1 = pollQuestionRepository.save(pollQuestion);
		pollRepository.save(poll);
		return pollQuestion1;
	}
}
