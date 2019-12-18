package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreatePollQuestionRequest {
	@NotBlank
	@Size(max = 255)
	private String title;

	@Valid
	@Size(min = 1, max = 50)
	private List<@NotBlank @Size(max = 255) String> answers;
}
