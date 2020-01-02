package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.service.exceptions.PollNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PollService {
	Poll createNewPoll(Long userId, String name) throws UserNotFoundException;

	List<Poll> getPollsByUser(Long userId, Pageable pageable);

	Optional<Poll> getById(Long id);

	Poll editPoll(Poll poll, String name);

	void incrementLaunchedCount(Long id, int delta) throws PollNotFoundException;

	void deletePoll(Poll poll);

	List<PollQuestion> addQuestions(Poll poll, Map<String, List<String>> titleToAnswersMap);

	PollQuestion editQuestion(PollQuestion pollQuestion, String title, Integer index, List<String> answers);

	void deleteQuestion(PollQuestion pollQuestion);

	Poll setQuestionsOrder(Poll poll, List<Long> qids) throws QuestionNotFoundException;

	Page<PollSession> getPollSessionsPage(Long pid, Pageable pageable);

	Optional<PollSession> getPollSession(Long pid, String sid);

	void deletePollSession(Long pid, String sid);

	Poll addTag(Poll poll, Tag tag);

	Poll removeTag(Poll poll, Tag tag);

	Page<Poll> getPollsByTags(List<Tag> tag, Pageable pageable);
}
