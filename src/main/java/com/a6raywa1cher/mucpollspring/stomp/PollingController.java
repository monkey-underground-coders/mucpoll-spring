package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.service.exceptions.AnswerNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.QuestionNotFoundException;
import com.a6raywa1cher.mucpollspring.service.exceptions.TemporaryPollSessionNotFound;
import com.a6raywa1cher.mucpollspring.service.interfaces.PollService;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import com.a6raywa1cher.mucpollspring.stomp.exceptions.ForbiddenException;
import com.a6raywa1cher.mucpollspring.stomp.request.AppendNewVote;
import com.a6raywa1cher.mucpollspring.stomp.request.ChangeQuestionRequest;
import com.a6raywa1cher.mucpollspring.stomp.response.CurrentSessionInfoResponse;
import com.a6raywa1cher.mucpollspring.stomp.response.OpenVotingSessionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Controller
@Slf4j
public class PollingController {
	private VotingService votingService;
	private PollService pollService;

	@Autowired
	public PollingController(VotingService votingService, PollService pollService) {
		this.votingService = votingService;
		this.pollService = pollService;
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
			try {
				response = new CurrentSessionInfoResponse(temporaryPollSession);
			} catch (JsonProcessingException e) {
				log.error("json read error", e);
				response = null;
			}
		}
		return response;
	}

	@MessageMapping("/vote/{pid}/{sid}/append")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse appendVote(@DestinationVariable("pid") Long pid,
	                                             @DestinationVariable("sid") String sid,
	                                             @Payload AppendNewVote vote) {
		try {
			votingService.appendVote(sid, vote.getAid());
			TemporaryPollSession temporaryPollSession = votingService.getBySid(sid).get();
			return new CurrentSessionInfoResponse(temporaryPollSession);
		} catch (TemporaryPollSessionNotFound | AnswerNotFoundException | JsonProcessingException temporaryPollSessionNotFound) {
			return null;
		}
	}

	@MessageMapping("/polladmin/{pid}/openvote")
	@SendTo("/topic/{pid}")
	@Transactional
	public OpenVotingSessionResponse openVotingSession(@DestinationVariable("pid") Long pid,
	                                                   @Header("simpSessionId") String sessionId,
	                                                   Principal principal) {
		Optional<Poll> poll = pollService.getById(pid);
		if (poll.isEmpty()) {
			return new OpenVotingSessionResponse();
		}
		if (!poll.get().getCreator().getUsername().equals(principal.getName())) {
			throw new ForbiddenException();
		}
		TemporaryPollSession temporaryPollSession = votingService.createNewTemporaryPollSession(
				poll.get(),
				poll.get().getCreator().getId(),
				sessionId);
		OpenVotingSessionResponse openVotingSessionResponse = new OpenVotingSessionResponse();
		openVotingSessionResponse.setSid(temporaryPollSession.getId());
		return openVotingSessionResponse;
	}

	@MessageMapping("/polladmin/{pid}/{sid}/start")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse start(@DestinationVariable("pid") Long pid,
	                                        @DestinationVariable("sid") String sid,
	                                        Principal principal) throws IOException {
		Optional<TemporaryPollSession> temporaryPollSession = votingService.getBySid(sid);
		if (temporaryPollSession.isEmpty()) {
			return null;
		}
		Poll poll = temporaryPollSession.get().deserializePoll();
		if (!poll.getCreator().getUsername().equals(principal.getName())) {
			throw new ForbiddenException();
		}
		try {
			TemporaryPollSession updated = votingService.start(sid);
			return new CurrentSessionInfoResponse(updated);
		} catch (TemporaryPollSessionNotFound temporaryPollSessionNotFound) {
			return null;
		}
	}

	@MessageMapping("/polladmin/{pid}/{sid}/change_question")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse changeQuestion(@DestinationVariable("pid") Long pid,
	                                                 @DestinationVariable("sid") String sid,
	                                                 @Payload ChangeQuestionRequest request,
	                                                 Principal principal) throws IOException {
		Optional<TemporaryPollSession> temporaryPollSession = votingService.getBySid(sid);
		if (temporaryPollSession.isEmpty()) {
			return null;
		}
		Poll poll = temporaryPollSession.get().deserializePoll();
		if (!poll.getCreator().getUsername().equals(principal.getName())) {
			throw new ForbiddenException();
		}
		try {
			TemporaryPollSession updated = votingService.changeQuestion(sid, request.getQid());
			return new CurrentSessionInfoResponse(updated);
		} catch (TemporaryPollSessionNotFound | QuestionNotFoundException temporaryPollSessionNotFound) {
			return null;
		}
	}

	@MessageMapping("/polladmin/{pid}/{sid}/stopvote")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse stopVote(@DestinationVariable("pid") Long pid,
	                                           @DestinationVariable("sid") String sid,
	                                           Principal principal) throws IOException {
		Optional<TemporaryPollSession> temporaryPollSession = votingService.getBySid(sid);
		if (temporaryPollSession.isEmpty()) {
			return null;
		}
		Poll poll = temporaryPollSession.get().deserializePoll();
		if (!poll.getCreator().getUsername().equals(principal.getName())) {
			throw new ForbiddenException();
		}
		try {
			PollSession pollSession = votingService.closeVote(sid);
			return new CurrentSessionInfoResponse(pollSession);
		} catch (TemporaryPollSessionNotFound temporaryPollSessionNotFound) {
			return null;
		}
	}
}
