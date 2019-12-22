package com.a6raywa1cher.mucpollspring.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class DeletePollQuestionRequest {
	@NotNull
	@PositiveOrZero
	private Long qid;
}
