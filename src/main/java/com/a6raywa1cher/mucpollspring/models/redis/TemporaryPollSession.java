package com.a6raywa1cher.mucpollspring.models.redis;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
		pollSerialized = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.writeValueAsString(poll);
	}

	public Poll deserializePoll() throws IOException {
		return new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.readValue(pollSerialized, Poll.class);
	}
}
