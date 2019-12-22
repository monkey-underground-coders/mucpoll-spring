package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PollService {
	Poll createNewPoll(Long userId, String name) throws UserNotFoundException;

	List<Poll> getPollsByUser(Long userId, Pageable pageable);

	Optional<Poll> getById(Long id);

	Poll editPoll(Poll poll, String name);

	void deletePoll(Poll poll);

	PollQuestion addQuestion(Poll poll, String title, List<String> answers);

	PollQuestion editQuestion(PollQuestion pollQuestion, String title, Integer index, List<String> answers);

	void deleteQuestion(PollQuestion pollQuestion);

	Page<PollSession> getPollSessionsPage(Long pid, Pageable pageable);

//	PollQuestion updateQuestion(PollQuestion pollQuestion);
}
