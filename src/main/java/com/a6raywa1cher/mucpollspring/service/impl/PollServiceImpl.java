package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.file.PollSessionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionAnswerRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollQuestionRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.*;
import com.a6raywa1cher.mucpollspring.service.exceptions.PollNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.TagService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollServiceImpl implements PollService {
	private UserRepository userRepository;
	private PollRepository pollRepository;
	private PollQuestionRepository pollQuestionRepository;
	private PollQuestionAnswerRepository pollQuestionAnswerRepository;
	private PollSessionRepository pollSessionRepository;
	private TagService tagService;

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
		poll.setLaunchedCount(0);
		poll.setTags(Collections.emptyList());
		poll.setCreatedAt(LocalDateTime.now());
		return pollRepository.save(poll);
	}

	@Override
	public Page<Poll> getPollsByUser(Long userId, Pageable pageable) {
		return pollRepository.getAllByCreatorId(userId, pageable);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void reconstructPoll(Poll poll, String title, List<Pair<String, List<String>>> list, List<Tag> tags) {
		poll = this.editPoll(poll, title);
		poll = this.deleteAllQuestions(poll);
		poll = this.addQuestions(poll, list);
		poll = this.removeAllTags(poll);
		for (Tag tag : tags) {
			poll = this.addTag(poll, tag);
		}
		pollRepository.save(poll);
	}

	@Override
//	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	@Transactional
	public Optional<Poll> getById(Long id) {
		Optional<Poll> byId = pollRepository.findById(id);
		return byId;
	}

	@Override
	@Transactional
	public Poll editPoll(Poll poll, String name) {
		poll.setName(name);
		return pollRepository.save(poll);
	}

	@Override
	public void incrementLaunchedCount(Long id, int delta) throws PollNotFoundException {
		Optional<Poll> optionalPoll = pollRepository.findById(id);
		if (optionalPoll.isEmpty()) {
			throw new PollNotFoundException(id);
		}
		pollRepository.incrementLaunchedCount(optionalPoll.get(), delta);
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
	public Poll addQuestions(Poll poll, List<Pair<String, List<String>>> titleAndAnswers) {
		List<PollQuestion> newPollQuestions = new ArrayList<>(titleAndAnswers.size());
		int index = pollQuestionRepository.getMaxIndex(poll).orElse(-1) + 1;
		for (Pair<String, List<String>> entry : titleAndAnswers) {
			String title = entry.getFirst();
			List<String> answers = entry.getSecond();
			PollQuestion pollQuestion = new PollQuestion();
			pollQuestion.setPoll(poll);
			pollQuestion.setQuestion(title);
			pollQuestion.setIndex(index++);
			listToPQA(answers, pollQuestion);
			newPollQuestions.add(pollQuestion);
		}
		pollQuestionRepository.saveAll(newPollQuestions);
		return pollRepository.save(poll);
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
	@Transactional
	public Poll deleteAllQuestions(Poll poll) {
		pollQuestionRepository.deleteAll(poll.getQuestions());
		poll.getQuestions().clear();
		return pollRepository.save(poll);
	}

	@Override
	public Poll setQuestionsOrder(Poll poll, List<Long> qids) throws QuestionNotFoundException {
		List<PollQuestion> pollQuestionList = qids.stream()
				.map(pollQuestionRepository::findById)
				.map(Optional::orElseThrow)
				.collect(Collectors.toList());
		Optional<PollQuestion> error = pollQuestionList.stream().filter(pq -> !poll.getQuestions().contains(pq)).findAny();
		if (error.isPresent()) {
			throw new QuestionNotFoundException(error.get().getId());
		}
		normalizeIndexes(pollQuestionList);
		poll.setQuestions(pollQuestionList);
		pollQuestionRepository.saveAll(pollQuestionList);
		return pollRepository.save(poll);
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
		pollRepository.incrementLaunchedCount(pid, -1);
	}

	@Override
	@Transactional
	public Poll addTag(Poll poll, Tag tag) {
		poll.getTags().add(tag);
		return pollRepository.save(poll);
	}

	@Override
	public Poll removeTag(Poll poll, Tag tag) {
		if (poll.getTags().remove(tag)) {
			tag.getPollList().remove(poll);
			if (tag.getPollList().size() == 0) {
				tagService.deleteTag(tag);
			}
		}
		return pollRepository.save(poll);
	}

	@Override
	@Transactional
	public Poll removeAllTags(Poll poll) {
		for (Tag tag : poll.getTags()) {
			if (poll.getTags().remove(tag)) {
				tag.getPollList().remove(poll);
				if (tag.getPollList().size() == 0) {
					tagService.deleteTag(tag);
				}
			}
		}
		return pollRepository.save(poll);
	}

	@Override
	public Page<Poll> getPollsByTags(List<Tag> tag, Pageable pageable) {
		return pollRepository.getAllByTagsIn(tag, pageable);
	}
}
