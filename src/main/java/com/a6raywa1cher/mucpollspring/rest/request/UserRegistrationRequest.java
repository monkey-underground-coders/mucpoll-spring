package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRegistrationRequest {
	@NotBlank
	@Size(max = 100)
	private String username;

	@NotBlank
	@Size(max = 255)
	private String password;
}
