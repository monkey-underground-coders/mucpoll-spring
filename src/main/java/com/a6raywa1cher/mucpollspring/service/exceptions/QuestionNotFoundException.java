package com.a6raywa1cher.mucpollspring.service.exceptions;

public class QuestionNotFoundException extends NotFoundException {
	private Long qid;

	public QuestionNotFoundException(Long qid) {
		this.qid = qid;
	}

	@Override
	public Long getQuery() {
		return qid;
	}
}
