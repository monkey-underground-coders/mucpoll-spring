package com.a6raywa1cher.mucpollspring.stomp.response;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CurrentSessionInfoResponse {
	private boolean open;
	private boolean started;
	private Long currentQid;
	private JsonNode pollInfo;
	private List<AnswerAndCurrentCount> answers;

	public CurrentSessionInfoResponse(TemporaryPollSession temporaryPollSession) throws JsonProcessingException {
		this.setStarted(temporaryPollSession.isStarted());
		this.setCurrentQid(temporaryPollSession.getCurrentQid());
		this.setOpen(true);
		TemporaryPollSessionQuestion question = temporaryPollSession.getQuestions().stream()
				.filter(q -> q.getQid() == temporaryPollSession.getCurrentQid())
				.findFirst().orElseThrow();
		this.setPollInfo(new ObjectMapper().readTree(temporaryPollSession.getPollSerialized()));
		this.setAnswers(question.getMap().stream()
				.map(a -> new CurrentSessionInfoResponse.AnswerAndCurrentCount(a.getAid(), a.getCount()))
				.collect(Collectors.toList()));
	}

	public CurrentSessionInfoResponse(PollSession pollSession) {
		open = false;
		started = false;
		currentQid = null;
		pollInfo = null;
		answers = Collections.emptyList();
	}

	@Data
	public static final class AnswerAndCurrentCount {
		private long aid;
		private long count;

		public AnswerAndCurrentCount(long aid, long count) {
			this.aid = aid;
			this.count = count;
		}
	}
}
