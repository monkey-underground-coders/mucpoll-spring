package com.a6raywa1cher.mucpollspring.models.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@RedisHash("PollSessionQuestion")
@Data
public class TemporaryPollSessionQuestion {
	@Indexed
	private long id;
	@Indexed
	private long qid;
	private List<AnswerAndCount> map;
}
