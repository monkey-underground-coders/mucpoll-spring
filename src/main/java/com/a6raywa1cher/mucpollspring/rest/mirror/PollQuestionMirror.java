package com.a6raywa1cher.mucpollspring.rest.mirror;

import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PollQuestionMirror {
	private Long id;

	private Long pid;

	private Integer index;

	private String question;

	private List<PollQuestionAnswerMirror> answerOptions;

	public static PollQuestionMirror convert(PollQuestion pollQuestion) {
		PollQuestionMirror mirror = new PollQuestionMirror();
		mirror.setId(pollQuestion.getId());
		mirror.setPid(pollQuestion.getPoll().getId());
		mirror.setIndex(pollQuestion.getIndex());
		mirror.setQuestion(pollQuestion.getQuestion());
		mirror.setAnswerOptions(pollQuestion.getAnswerOptions().stream()
				.map(PollQuestionAnswerMirror::convert)
				.sorted(Comparator.comparingInt(PollQuestionAnswerMirror::getIndex))
				.collect(Collectors.toList()));
		return mirror;
	}
}
