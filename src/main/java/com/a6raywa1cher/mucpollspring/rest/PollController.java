package com.a6raywa1cher.mucpollspring.rest;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.rest.exception.PollNotFoundException;
import com.a6raywa1cher.mucpollspring.rest.exception.PollQuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.rest.exception.TagNotFoundException;
import com.a6raywa1cher.mucpollspring.rest.mirror.PollMirror;
import com.a6raywa1cher.mucpollspring.rest.request.*;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.TagService;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/poll")
public class PollController {
	private PollService pollService;
	private UserService userService;
	private TagService tagService;

	@Autowired
	public PollController(PollService pollService, UserService userService, TagService tagService) {
		this.pollService = pollService;
		this.userService = userService;
		this.tagService = tagService;
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
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<PollMirror> addQuestion(@PathVariable long pid, @RequestBody @Valid List<CreatePollQuestionRequest> dto) {
		Poll poll = $getPoll(pid);
		Poll updated = pollService.addQuestions(poll, dto.stream()
				.map(cpqr -> Pair.of(cpqr.getTitle(), cpqr.getAnswers()))
				.collect(Collectors.toList())
		);
		return ResponseEntity.ok(PollMirror.convert(updated, true));
	}

	@PutMapping("/{pid:[0-9]+}/question")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	@Transactional(rollbackOn = PollQuestionNotFoundException.class)
	public ResponseEntity<PollMirror> editQuestion(@PathVariable long pid,
	                                               @RequestBody @Valid List<EditPollQuestionRequest> dto)
			throws PollQuestionNotFoundException {
		Poll poll = $getPoll(pid);
		for (EditPollQuestionRequest req : dto) {
			Optional<PollQuestion> optionalPollQuestion = poll.getQuestions().stream()
					.filter(q -> q.getId().equals(req.getQid()))
					.findAny();
			if (optionalPollQuestion.isEmpty()) {
				throw new PollQuestionNotFoundException();
			}
			PollQuestion pollQuestion = optionalPollQuestion.get();
			pollService.editQuestion(pollQuestion, req.getTitle(), req.getIndex(), req.getAnswers());
		}
		return ResponseEntity.ok(PollMirror.convert($getPoll(pid), true));
	}

	@DeleteMapping("/{pid:[0-9]+}/question")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
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

	@PostMapping("/{pid:[0-9]+}/questions_order")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<PollMirror> changeQuestionsOrder(@PathVariable long pid, @RequestBody @Valid List<Long> dto) {
		Poll poll = $getPoll(pid);
		try {
			return ResponseEntity.ok(PollMirror.convert(pollService.setQuestionsOrder(poll, dto), true));
		} catch (QuestionNotFoundException e) {
			return ResponseEntity.badRequest().build();
		}
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
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<List<PollSession>> getHistory(@PathVariable long pid, Pageable pageable) {
		$getPoll(pid);
		return ResponseEntity.ok(pollService.getPollSessionsPage(pid, pageable).toList());
	}

	@GetMapping("/{pid:[0-9]+}/history/{sid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<PollSession> getPollSession(@PathVariable long pid, @PathVariable String sid) {
		$getPoll(pid);
		Optional<PollSession> optionalPollSession = pollService.getPollSession(pid, sid);
		if (optionalPollSession.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(optionalPollSession.get());
	}

	@DeleteMapping("/{pid:[0-9]+}/history/{sid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
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
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<PollMirror> getPoll(@PathVariable long pid) {
		Poll poll = $getPoll(pid);
		return ResponseEntity.ok(PollMirror.convert(poll, true));
	}

	@PutMapping("/{pid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<Void> reconstructPoll(@PathVariable long pid,
	                                            @RequestBody @Valid ReconstructPollRequest dto) {
		Poll poll = $getPoll(pid);
		pollService.reconstructPoll(poll, poll.getName(), dto.getList().stream()
				.map(cpqr -> Pair.of(cpqr.getTitle(), cpqr.getAnswers()))
				.collect(Collectors.toList()), dto.getTags().stream()
				.map(tid -> tagService.getById(tid).orElseThrow(TagNotFoundException::new))
				.collect(Collectors.toList())
		);
		return ResponseEntity.ok().build(); //TODO: fix bug
	}

	@PatchMapping("/{pid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<PollMirror> editPoll(@PathVariable long pid, @RequestBody @Valid EditPollRequest dto) {
		Poll poll = $getPoll(pid);
		return ResponseEntity.ok(PollMirror.convert(pollService.editPoll(poll, dto.getName()), true));
	}

	@DeleteMapping("/{pid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkPid(authentication,#pid)")
	public ResponseEntity<Void> deletePoll(@PathVariable long pid) {
		Poll poll = $getPoll(pid);
		pollService.deletePoll(poll);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/tags")
	public ResponseEntity<List<PollMirror>> getPageByTags(Pageable pageable, @RequestBody @Valid List<Long> tids) {
		List<Tag> tags = new ArrayList<>(tids.size());
		for (long tid : tids) {
			Tag tag = $getTag(tid);
			if (!tag.getCreator().getUsername().equals(getUserDetails().getUsername())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			tags.add(tag);
		}
		return ResponseEntity.ok(pollService.getPollsByTags(tags, pageable).stream()
				.map(p -> PollMirror.convert(p, false))
				.collect(Collectors.toList())
		);
	}

	private Poll $getPoll(long pid) {
		Optional<Poll> optionalPoll = pollService.getById(pid);
		if (optionalPoll.isEmpty()) {
			throw new PollNotFoundException();
		}
		return optionalPoll.get();
	}

	private Tag $getTag(long tid) {
		Optional<Tag> optionalTag = tagService.getById(tid);
		if (optionalTag.isEmpty()) {
			throw new TagNotFoundException();
		}
		return optionalTag.get();
	}
}
