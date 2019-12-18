package com.a6raywa1cher.mucpollspring.models.sql;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class PollQuestion {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	private Poll poll;

	@Column(nullable = false)
	private Integer index;

	@Column
	private String question;

	@OneToMany(mappedBy = "pollQuestion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PollQuestionAnswer> answerOptions;
}
