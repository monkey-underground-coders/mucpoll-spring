package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.stomp.request.AppendNewVote;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class PollingController {
	@MessageMapping("/vote/{pid}")
	public void putNewVote(@DestinationVariable String pid,
	                       @Payload AppendNewVote vote,
	                       @Headers Map<String, Object> headers, Principal principal) {
		System.out.println("VOTE! " + vote.toString());
	}
}
