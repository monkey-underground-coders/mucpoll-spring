package com.a6raywa1cher.mucpollspring.models.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PollSessionAnswerAndCount {
	private Long aid;

	private String answer;

	private Long count;
}
