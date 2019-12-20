package com.a6raywa1cher.mucpollspring.stomp.response;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CurrentSessionInfoResponse {
	private boolean open;
	private Long currentQid;
	private List<AnswerAndCurrentCount> answers;

	public CurrentSessionInfoResponse(TemporaryPollSession temporaryPollSession) {
		this.setCurrentQid(temporaryPollSession.getCurrentQid());
		this.setOpen(true);
		TemporaryPollSessionQuestion question = temporaryPollSession.getQuestions().stream()
				.filter(q -> q.getQid() == temporaryPollSession.getCurrentQid())
				.findFirst().orElseThrow();
		this.setAnswers(question.getMap().stream()
				.map(a -> new CurrentSessionInfoResponse.AnswerAndCurrentCount(a.getAid(), a.getCount()))
				.collect(Collectors.toList()));
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
