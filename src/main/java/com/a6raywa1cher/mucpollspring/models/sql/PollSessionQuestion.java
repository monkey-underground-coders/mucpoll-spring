package com.a6raywa1cher.mucpollspring.models.sql;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
public class PollSessionQuestion {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private PollSession pollSession;

	@ManyToOne(optional = false)
	private PollQuestion pollQuestion;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "ANSWER_COUNTS", joinColumns = @JoinColumn(name = "QUESTION_ID"))
	@MapKeyJoinColumn(name = "ANSWER_ID")
	@Column(name = "COUNT")
	private Map<PollQuestionAnswer, Integer> recordedData;
}
