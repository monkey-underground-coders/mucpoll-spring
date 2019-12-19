package com.a6raywa1cher.mucpollspring.stomp.response;

import lombok.Data;

import java.util.List;

@Data
public class CurrentSessionInfoResponse {
	private boolean open;
	private Long currentQid;
	private List<AnswerAndCurrentCount> answers;

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
