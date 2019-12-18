package com.a6raywa1cher.mucpollspring.rest.mirror;

import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import lombok.Data;

@Data
public class PollQuestionAnswerMirror {
	private Long id;

	private Long pqid;

	private Integer index;

	private String answer;

	public static PollQuestionAnswerMirror convert(PollQuestionAnswer pollQuestionAnswer) {
		PollQuestionAnswerMirror mirror = new PollQuestionAnswerMirror();
		mirror.setId(pollQuestionAnswer.getId());
		mirror.setIndex(pollQuestionAnswer.getIndex());
		mirror.setAnswer(pollQuestionAnswer.getAnswer());
		mirror.setPqid(pollQuestionAnswer.getPollQuestion().getId());
		return mirror;
	}
}
