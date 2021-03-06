package com.a6raywa1cher.mucpollspring.rest.mirror;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PollMirror {
	private Long id;

	private String name;

	private List<PollQuestionMirror> questions;

	private UserMirror creator;

	private int launchedCount;

	private List<TagMirror> tags;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdAt;

	public static PollMirror convert(Poll poll, boolean includeQuestions) {
		PollMirror mirror = new PollMirror();
		mirror.setId(poll.getId());
		mirror.setName(poll.getName());
		mirror.setQuestions(includeQuestions ?
				poll.getQuestions().stream()
						.map(PollQuestionMirror::convert)
						.sorted(Comparator.comparingInt(PollQuestionMirror::getIndex))
						.collect(Collectors.toList()) :
				Collections.emptyList());
		mirror.setCreator(UserMirror.convert(poll.getCreator()));
		mirror.setLaunchedCount(poll.getLaunchedCount());
		mirror.setTags(poll.getTags().stream().map(t -> TagMirror.convert(t, false)).collect(Collectors.toList()));
		mirror.setCreatedAt(poll.getCreatedAt());
		return mirror;
	}
}
