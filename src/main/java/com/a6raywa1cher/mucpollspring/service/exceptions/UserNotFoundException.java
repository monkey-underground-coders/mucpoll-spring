package com.a6raywa1cher.mucpollspring.service.exceptions;

public class UserNotFoundException extends NotFoundException {
	private final Long userId;

	public UserNotFoundException(Long userId) {
		this.userId = userId;
	}

	@Override
	public Long getQuery() {
		return userId;
	}
}
