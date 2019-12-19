package com.a6raywa1cher.mucpollspring.models.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("AnswerAndCount")
@Data
public class AnswerAndCount {
	@Id
	private String id;
	@Indexed
	private long aid;
	private long count;
}
