package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class TagGrantedAuthority implements GrantedAuthority {
	private final long tid;

	public TagGrantedAuthority(long tid) {
		this.tid = tid;
	}

	@Override
	public String getAuthority() {
		return "TID_" + tid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagGrantedAuthority that = (TagGrantedAuthority) o;
		return tid == that.tid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(tid);
	}

	@Override
	public String toString() {
		return getAuthority();
	}
}
