package com.a6raywa1cher.mucpollspring.models.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@RedisHash("PollSession")
@Data
public class TemporaryPollSession {
	@Id
	private String id;
	@Indexed
	private long pid;
	private Long currentQid;
	private List<TemporaryPollSessionQuestion> questions;
}
