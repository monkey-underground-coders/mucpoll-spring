package com.a6raywa1cher.mucpollspring.models.sql;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PollQuestionAnswer {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private PollQuestion pollQuestion;

	@Column
	private String answer;
}
