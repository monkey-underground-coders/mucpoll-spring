package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;

import java.util.List;
import java.util.Optional;

public interface PollService {
	Poll createNewPoll(Long userId, String name);

//	Poll updatePoll(Poll poll);

	List<Poll> getPollsByUser(Long userId);

	Optional<Poll> getById(Long id);

	PollQuestion addQuestion(Poll poll, String title, List<String> answers);

//	PollQuestion updateQuestion(PollQuestion pollQuestion);
}
