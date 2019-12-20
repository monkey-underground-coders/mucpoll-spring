package com.a6raywa1cher.mucpollspring.rest;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.rest.mirror.PollMirror;
import com.a6raywa1cher.mucpollspring.rest.request.CreatePollQuestionRequest;
import com.a6raywa1cher.mucpollspring.rest.request.CreatePollRequest;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/poll")
public class PollController {
	private PollService pollService;
	private UserService userService;

	@Autowired
	public PollController(PollService pollService, UserService userService) {
		this.pollService = pollService;
		this.userService = userService;
	}

	private UserDetails getUserDetails() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@SneakyThrows
	@PostMapping("/create")
	public ResponseEntity<PollMirror> createPoll(@RequestBody @Valid CreatePollRequest dto) {
		Poll poll = pollService.createNewPoll(
				userService.getByUsername(getUserDetails().getUsername()).orElseThrow().getId(), dto.getName()
		);
		return ResponseEntity.ok(PollMirror.convert(poll, false));
	}

	@PostMapping("/{pid:[0-9]+}/add_question")
	public ResponseEntity<PollMirror> addQuestion(@PathVariable long pid, @RequestBody @Valid CreatePollQuestionRequest dto) {
		Optional<Poll> optionalPoll = pollService.getById(pid);
		if (optionalPoll.isEmpty() || !optionalPoll.get().getCreator().getUsername().equals(getUserDetails().getUsername())) {
			return ResponseEntity.badRequest().build();
		}
		PollQuestion pollQuestion = pollService.addQuestion(optionalPoll.get(), dto.getTitle(), dto.getAnswers());
		return ResponseEntity.ok(PollMirror.convert(pollQuestion.getPoll(), true));
	}
}
