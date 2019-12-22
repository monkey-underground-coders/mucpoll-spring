package com.a6raywa1cher.mucpollspring.models.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollSessionAnswerAndCount {
	private Long aid;

	private String answer;

	private Long count;
}
