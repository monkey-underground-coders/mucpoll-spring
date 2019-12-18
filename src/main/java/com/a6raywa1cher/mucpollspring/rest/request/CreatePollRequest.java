package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreatePollRequest {
	@NotBlank
	@Size(max = 255)
	private String name;
}
