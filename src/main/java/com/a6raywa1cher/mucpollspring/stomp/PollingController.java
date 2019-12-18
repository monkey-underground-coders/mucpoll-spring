package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import com.a6raywa1cher.mucpollspring.stomp.request.AppendNewVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class PollingController {
	private VotingService votingService;

	@Autowired
	public PollingController(VotingService votingService) {
		this.votingService = votingService;
	}

	@MessageMapping("/vote")
	public void putNewVote(@Payload AppendNewVote vote,
	                       @Headers Map<String, Object> headers, Principal principal) {
		System.out.println("VOTE! " + vote.toString());
		votingService.createNewTemporaryPollSession(vote.getPid());
	}
}
