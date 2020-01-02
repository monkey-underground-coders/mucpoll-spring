package com.a6raywa1cher.mucpollspring.rest;

import com.a6raywa1cher.mucpollspring.config.security.MvcAccessChecker;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.rest.mirror.TagMirror;
import com.a6raywa1cher.mucpollspring.rest.request.CreateTagRequest;
import com.a6raywa1cher.mucpollspring.rest.request.EditTagRequest;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.TagService;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tag")
public class TagController {
	private PollService pollService;
	private TagService tagService;
	private UserService userService;
	private MvcAccessChecker accessChecker;

	@Autowired
	public TagController(PollService pollService, TagService tagService,
	                     UserService userService, MvcAccessChecker accessChecker) {
		this.pollService = pollService;
		this.tagService = tagService;
		this.userService = userService;
		this.accessChecker = accessChecker;
	}

	private UserDetails getUserDetails() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@SneakyThrows
	@GetMapping("/tags")
	public ResponseEntity<List<TagMirror>> getAllTags() {
		UserDetails userDetails = getUserDetails();
		User user = userService.getByUsername(userDetails.getUsername()).orElseThrow();
		return ResponseEntity.ok(tagService.getAllByUser(user.getId()).stream()
				.map(t -> TagMirror.convert(t, true))
				.collect(Collectors.toList())
		);
	}

	@PostMapping("/create")
	public ResponseEntity<TagMirror> createTag(@RequestBody @Valid CreateTagRequest dto) {
		Optional<Poll> optionalPoll = pollService.getById(dto.getFirstPid());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (optionalPoll.isEmpty() || !accessChecker.checkPid(authentication, dto.getFirstPid())) {
			return ResponseEntity.badRequest().build();
		}
		Tag tag = tagService.createTag(optionalPoll.get(),
				dto.getName(),
				userService.getByUsername(getUserDetails().getUsername()).orElseThrow()
		);
		return ResponseEntity.ok(TagMirror.convert(tag, true));
	}

	@GetMapping("/{tid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkTid(authentication,#tid)")
	public ResponseEntity<TagMirror> getTag(@PathVariable Long tid) {
		Optional<Tag> optionalTag = tagService.getById(tid);
		if (optionalTag.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(TagMirror.convert(optionalTag.get(), true));
	}

	@PutMapping("/{tid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkTid(authentication,#tid)")
	public ResponseEntity<TagMirror> editTag(@PathVariable Long tid, @RequestBody @Valid EditTagRequest dto) {
		Optional<Tag> optionalTag = tagService.getById(tid);
		if (optionalTag.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(TagMirror.convert(tagService.editTag(optionalTag.get(), dto.getName()), true));
	}

	@DeleteMapping("/{tid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkTid(authentication,#tid)")
	@Transactional
	public ResponseEntity<Void> deleteTag(@PathVariable Long tid) {
		Optional<Tag> optionalTag = tagService.getById(tid);
		if (optionalTag.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		tagService.deleteTag(optionalTag.get());
		return ResponseEntity.ok().build();
	}
}
