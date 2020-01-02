package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class CreateTagRequest {
	@NotBlank
	@Size(max = 255)
	private String name;
	@NotNull
	@PositiveOrZero
	private Long firstPid;
}
