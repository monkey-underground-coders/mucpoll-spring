package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditPollRequest {
	@NotBlank
	private String name;
}
