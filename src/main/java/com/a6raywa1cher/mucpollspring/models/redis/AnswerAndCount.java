package com.a6raywa1cher.mucpollspring.models.redis;

import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;

@Data
public class AnswerAndCount {
	@Indexed
	private long aid;
	private long count;
}
