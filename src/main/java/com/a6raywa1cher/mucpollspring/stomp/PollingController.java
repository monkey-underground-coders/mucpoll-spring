package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
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
import java.util.stream.Collectors;

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

	@MessageMapping("/voteinfo/{pid}/{sid}")
	@SendTo("/topic/{pid}/{sid}")
	public CurrentSessionInfoResponse voteJoin(@DestinationVariable("pid") Long pid,
	                                           @DestinationVariable("sid") String sid) {
		Optional<TemporaryPollSession> optionalTemporaryPollSession = votingService.getBySid(sid);
		CurrentSessionInfoResponse response = new CurrentSessionInfoResponse();
		if (optionalTemporaryPollSession.isEmpty()) {
			response.setOpen(false);
			response.setCurrentQid(null);
			response.setAnswers(Collections.emptyList());
		} else {
			TemporaryPollSession temporaryPollSession = optionalTemporaryPollSession.get();
			response.setCurrentQid(temporaryPollSession.getCurrentQid());
			response.setOpen(true);
			TemporaryPollSessionQuestion question = temporaryPollSession.getQuestions().stream()
					.filter(q -> q.getQid() == temporaryPollSession.getCurrentQid())
					.findFirst().orElseThrow();
			response.setAnswers(question.getMap().stream()
					.map(a -> new CurrentSessionInfoResponse.AnswerAndCurrentCount(a.getAid(), a.getCount()))
					.collect(Collectors.toList()));
		}
		return response;
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
