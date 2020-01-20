package com.a6raywa1cher.mucpollspring.models.file;

import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSession;
import com.a6raywa1cher.mucpollspring.models.redis.TemporaryPollSessionQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestionAnswer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PollSession {
	private String sid;

	private JsonNode pollInfo;

	private Long pid;

	private List<PollSessionQuestion> recordedQuestions;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime startedAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime recordedAt;

	public PollSession(TemporaryPollSession tps, Poll poll) {
		sid = tps.getId();
		this.pollInfo = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.valueToTree(poll);
		pid = tps.getPid();
		startedAt = tps.getCreatedAt();
		recordedAt = LocalDateTime.now();
		Map<Long, PollQuestion> qidToPQ = poll.getQuestions().stream()
				.collect(Collectors.toMap(PollQuestion::getId, Function.identity()));

		recordedQuestions = tps.getQuestions().stream()
				.map(tpsq -> Pair.of(tpsq, qidToPQ.get(tpsq.getQid())))
				.sorted(Comparator.comparingInt(p -> p.getSecond().getIndex()))
				.map(p -> {
					TemporaryPollSessionQuestion tpsq = p.getFirst();
					PollQuestion pollQuestion = p.getSecond();
					PollSessionQuestion pollSessionQuestion = new PollSessionQuestion();
					pollSessionQuestion.setQid(tpsq.getQid());
					pollSessionQuestion.setQuestion(pollQuestion.getQuestion());
					Map<Long, String> aidToAnswer = pollQuestion.getAnswerOptions().stream()
							.collect(Collectors.toMap(PollQuestionAnswer::getId, PollQuestionAnswer::getAnswer));
					pollSessionQuestion.setRecordedData(tpsq.getMap().stream()
							.map(a -> new PollSessionAnswerAndCount(a.getAid(), aidToAnswer.get(a.getAid()), a.getCount()))
							.collect(Collectors.toList()));
					return pollSessionQuestion;
				})
				.collect(Collectors.toList());
	}
}
