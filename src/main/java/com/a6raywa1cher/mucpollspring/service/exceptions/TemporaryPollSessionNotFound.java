package com.a6raywa1cher.mucpollspring.service.exceptions;

public class TemporaryPollSessionNotFound extends NotFoundException {
	private final String sid;

	public TemporaryPollSessionNotFound(String sid) {
		this.sid = sid;
	}

	@Override
	public String getQuery() {
		return sid;
	}
}
