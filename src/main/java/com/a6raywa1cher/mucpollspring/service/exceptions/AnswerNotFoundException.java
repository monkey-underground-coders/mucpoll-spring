package com.a6raywa1cher.mucpollspring.service.exceptions;

public class AnswerNotFoundException extends NotFoundException {
	private final Long aid;

	public AnswerNotFoundException(Long aid) {
		this.aid = aid;
	}

	@Override
	public Long getQuery() {
		return aid;
	}
}
