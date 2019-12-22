package com.a6raywa1cher.mucpollspring.dao.repository.exception;

import java.io.IOException;

public class PollSessionNotFound extends Exception {
	private final Long pid;
	private final String sid;

	public PollSessionNotFound(Long pid, String sid) {
		super(String.format("PollSession with pid:%d and sid:%s not found", pid, sid));
		this.pid = pid;
		this.sid = sid;
	}

	public PollSessionNotFound(Long pid, String sid, IOException e) {
		super(String.format("PollSession with pid:%d and sid:%s not found", pid, sid), e);
		this.pid = pid;
		this.sid = sid;
	}
}
