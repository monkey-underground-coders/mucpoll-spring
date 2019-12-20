package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.models.redis.AnswerAndCount;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import com.a6raywa1cher.mucpollspring.stomp.exceptions.ForbiddenException;
import com.a6raywa1cher.mucpollspring.stomp.request.AppendNewVote;
import com.a6raywa1cher.mucpollspring.stomp.response.CurrentSessionInfoResponse;
import com.a6raywa1cher.mucpollspring.stomp.response.OpenVotingSessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller
public class PollingController {
	private VotingService votingService;
	private PollService pollService;

	@Autowired
	public PollingController(VotingService votingService, PollService pollService) {
		this.votingService = votingService;
		this.pollService = pollService;
	}

	@MessageMapping("/vote")
	public void putNewVote(@Payload AppendNewVote vote,
	                       @Headers Map<String, Object> headers, Principal principal) {
		System.out.println("VOTE! " + vote.toString());
//		votingService.createNewTemporaryPollSession(vote.getPid());
	}

	@MessageMapping("/vote/{pid}/{sid}/info")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse voteInfo(@DestinationVariable("pid") Long pid,
	                                           @DestinationVariable("sid") String sid) {
		Optional<TemporaryPollSession> optionalTemporaryPollSession = votingService.getBySid(sid);
		CurrentSessionInfoResponse response;
		if (optionalTemporaryPollSession.isEmpty()) {
			response = new CurrentSessionInfoResponse();
			response.setOpen(false);
			response.setCurrentQid(null);
			response.setAnswers(Collections.emptyList());
		} else {
			TemporaryPollSession temporaryPollSession = optionalTemporaryPollSession.get();
			response = new CurrentSessionInfoResponse(temporaryPollSession);
		}
		return response;
	}

	@MessageMapping("/vote/{pid}/{sid}/append")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse appendVote(@DestinationVariable("pid") Long pid,
	                                             @DestinationVariable("sid") String sid,
	                                             @Payload AppendNewVote vote) {
		try {
			AnswerAndCount answerAndCount = votingService.appendVote(sid, vote.getAid());
			TemporaryPollSession temporaryPollSession = votingService.getBySid(sid).get();
			return new CurrentSessionInfoResponse(temporaryPollSession);
		} catch (TemporaryPollSessionNotFound | AnswerNotFoundException temporaryPollSessionNotFound) {
			return null;
		}
	}

	@MessageMapping("/polladmin/{pid}/openvote")
	@SendTo("/topic/{pid}")
	@Transactional
	public OpenVotingSessionResponse openVotingSession(@DestinationVariable("pid") Long pid, Principal principal) {
		Optional<Poll> poll = pollService.getById(pid);
		if (poll.isEmpty()) {
			return new OpenVotingSessionResponse();
		}
		if (!poll.get().getCreator().getUsername().equals(principal.getName())) {
			throw new ForbiddenException();
		}
		TemporaryPollSession temporaryPollSession = votingService.createNewTemporaryPollSession(poll.get());
		OpenVotingSessionResponse openVotingSessionResponse = new OpenVotingSessionResponse();
		openVotingSessionResponse.setSid(temporaryPollSession.getId());
		return openVotingSessionResponse;
	}
}
