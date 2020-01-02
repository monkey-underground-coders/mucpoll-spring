package com.a6raywa1cher.mucpollspring.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid TID")
public class TagNotFoundException extends RuntimeException {
}
