package com.a6raywa1cher.mucpollspring.rest;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.rest.exception.PollNotFoundException;
import com.a6raywa1cher.mucpollspring.rest.mirror.PollMirror;
import com.a6raywa1cher.mucpollspring.rest.request.*;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@PostMapping("/{pid:[0-9]+}/question")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollMirror> addQuestion(@PathVariable long pid, @RequestBody @Valid CreatePollQuestionRequest dto) {
		Poll poll = $getPoll(pid);
		PollQuestion pollQuestion = pollService.addQuestion(poll, dto.getTitle(), dto.getAnswers());
		return ResponseEntity.ok(PollMirror.convert(pollQuestion.getPoll(), true));
	}

	@PutMapping("/{pid:[0-9]+}/question")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollMirror> editQuestion(@PathVariable long pid, @RequestBody @Valid EditPollQuestionRequest dto) {
		Poll poll = $getPoll(pid);
		Optional<PollQuestion> optionalPollQuestion = poll.getQuestions().stream()
				.filter(q -> q.getId().equals(dto.getQid()))
				.findAny();
		if (optionalPollQuestion.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		PollQuestion pollQuestion = optionalPollQuestion.get();
		PollQuestion updated = pollService.editQuestion(pollQuestion, dto.getTitle(), dto.getIndex(), dto.getAnswers());
		return ResponseEntity.ok(PollMirror.convert(updated.getPoll(), true));
	}

	@DeleteMapping("/{pid:[0-9]+}/question")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollMirror> deleteQuestion(@PathVariable long pid, @RequestBody @Valid DeletePollQuestionRequest dto) {
		Poll poll = $getPoll(pid);
		Optional<PollQuestion> optionalPollQuestion = poll.getQuestions().stream()
				.filter(q -> q.getId().equals(dto.getQid()))
				.findAny();
		if (optionalPollQuestion.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		PollQuestion pollQuestion = optionalPollQuestion.get();
		pollService.deleteQuestion(pollQuestion);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/polls")
	public ResponseEntity<List<PollMirror>> getPolls(Pageable pageable) {
		return ResponseEntity.ok(pollService.getPollsByUser(userService.getByUsername(getUserDetails().getUsername()).orElseThrow().getId(), pageable)
				.stream()
				.map(p -> PollMirror.convert(p, true))
				.collect(Collectors.toList())
		);
	}

	@GetMapping("/{pid:[0-9]+}/history")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<List<PollSession>> getHistory(@PathVariable long pid, Pageable pageable) {
		$getPoll(pid);
		return ResponseEntity.ok(pollService.getPollSessionsPage(pid, pageable).toList());
	}

	@GetMapping("/{pid:[0-9]+}/history/{sid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollSession> getPollSession(@PathVariable long pid, @PathVariable String sid) {
		$getPoll(pid);
		Optional<PollSession> optionalPollSession = pollService.getPollSession(pid, sid);
		if (optionalPollSession.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(optionalPollSession.get());
	}

	@DeleteMapping("/{pid:[0-9]+}/history/{sid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollSession> deletePollSession(@PathVariable long pid, @PathVariable String sid) {
		$getPoll(pid);
		Optional<PollSession> optionalPollSession = pollService.getPollSession(pid, sid);
		if (optionalPollSession.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		pollService.deletePollSession(pid, sid);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{pid:[0-9]+}")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollMirror> getPoll(@PathVariable long pid) {
		Poll poll = $getPoll(pid);
		return ResponseEntity.ok(PollMirror.convert(poll, true));
	}

	@PutMapping("/{pid:[0-9]+}")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<PollMirror> editPoll(@PathVariable long pid, @RequestBody EditPollRequest dto) {
		Poll poll = $getPoll(pid);
		return ResponseEntity.ok(PollMirror.convert(pollService.editPoll(poll, dto.getName()), true));
	}

	@DeleteMapping("/{pid:[0-9]+}")
	@PreAuthorize("@pollAccessChecker.check(authentication,#pid)")
	public ResponseEntity<Void> deletePoll(@PathVariable long pid) {
		Poll poll = $getPoll(pid);
		pollService.deletePoll(poll);
		return ResponseEntity.ok().build();
	}

	private Poll $getPoll(@PathVariable long pid) {
		Optional<Poll> optionalPoll = pollService.getById(pid);
		if (optionalPoll.isEmpty()) {
			throw new PollNotFoundException();
		}
		return optionalPoll.get();
	}
}
