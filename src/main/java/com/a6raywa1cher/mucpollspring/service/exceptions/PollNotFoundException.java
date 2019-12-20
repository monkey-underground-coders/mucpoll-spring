package com.a6raywa1cher.mucpollspring.service.exceptions;

public class PollNotFoundException extends NotFoundException {
	private final Long pid;

	public PollNotFoundException(Long pid) {
		this.pid = pid;
	}

	@Override
	public Long getQuery() {
		return pid;
	}
}
