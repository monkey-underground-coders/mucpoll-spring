package com.a6raywa1cher.mucpollspring.stomp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Forbidden")
public class ForbiddenException extends RuntimeException {
}
