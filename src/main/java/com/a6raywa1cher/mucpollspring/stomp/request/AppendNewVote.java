package com.a6raywa1cher.mucpollspring.stomp.request;

import lombok.Data;

@Data
public class AppendNewVote {
	private Long pid;
	private Long question;
}
