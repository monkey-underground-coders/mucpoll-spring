package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ReconstructPollRequest {
	@NotBlank
	@Size(max = 255)
	private String name;

	@Valid
	@NotNull
	@Size(min = 1, max = 255)
	private List<@Valid QuestionAndAnswers> list;

	@Valid
	@Size(max = 255)
	private List<@PositiveOrZero Long> tags;

	@Data
	public static class QuestionAndAnswers {
		@NotBlank
		@Size(max = 255)
		private String title;

		@Valid
		@NotNull
		@Size(min = 1, max = 50)
		private List<@NotBlank @Size(max = 255) String> answers;
	}
}
