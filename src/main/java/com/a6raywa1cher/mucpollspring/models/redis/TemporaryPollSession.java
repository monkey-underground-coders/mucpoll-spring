package com.a6raywa1cher.mucpollspring.models.redis;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("PollSession")
@Data
public class TemporaryPollSession {
	@Id
	private String id;
	@Indexed
	private long pid;
	@Indexed
	private long uid;
	private LocalDateTime createdAt;
	private Long currentQid;
	private List<TemporaryPollSessionQuestion> questions;
	private String pollSerialized;
	private boolean started;
	private String simpSessionId;

	public void serializePoll(Poll poll) throws IOException {
		pollSerialized = new ObjectMapper().writeValueAsString(poll);
	}

	public Poll deserializePoll() throws IOException {
		return new ObjectMapper().readValue(pollSerialized, Poll.class);
	}
}
