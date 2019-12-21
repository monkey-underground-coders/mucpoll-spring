package com.a6raywa1cher.mucpollspring.models.file;

import lombok.Data;

import java.util.List;

@Data
public class PollSessionQuestion {
	private Long qid;

	private String question;

	private List<PollSessionAnswerAndCount> recordedData;
}
