package com.a6raywa1cher.mucpollspring.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Invalid PID")
public class PollNotFoundException extends RuntimeException {
}
