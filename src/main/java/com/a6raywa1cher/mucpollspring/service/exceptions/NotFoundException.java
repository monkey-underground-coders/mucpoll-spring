package com.a6raywa1cher.mucpollspring.service.exceptions;

public abstract class NotFoundException extends Exception {
	public abstract Object getQuery();
}
