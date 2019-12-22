package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class PollGrantedAuthority implements GrantedAuthority {
	private final long pid;

	public PollGrantedAuthority(long pid) {
		this.pid = pid;
	}

	@Override
	public String getAuthority() {
		return "PID_" + pid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PollGrantedAuthority that = (PollGrantedAuthority) o;
		return pid == that.pid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pid);
	}

	@Override
	public String toString() {
		return getAuthority();
	}
}
