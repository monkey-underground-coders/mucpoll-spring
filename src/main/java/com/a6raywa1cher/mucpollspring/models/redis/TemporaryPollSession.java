package com.a6raywa1cher.mucpollspring.models.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("PollSession")
@Data
public class TemporaryPollSession implements Serializable {
	private long id;
	private long pid;

}
