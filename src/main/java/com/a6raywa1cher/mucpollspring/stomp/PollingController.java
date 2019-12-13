package com.a6raywa1cher.mucpollspring.stomp;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PollingController {
	@MessageMapping("/vote")
	public void getNewVote() {

	}
}
