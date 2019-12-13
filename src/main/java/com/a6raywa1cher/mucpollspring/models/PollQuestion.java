package com.a6raywa1cher.mucpollspring.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class PollQuestion {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Integer position;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	private Poll poll;

	@Column
	private String question;

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private List<PollQuestionAnswer> answerOptions;
}
