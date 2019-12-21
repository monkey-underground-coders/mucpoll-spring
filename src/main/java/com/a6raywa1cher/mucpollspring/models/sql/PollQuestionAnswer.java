package com.a6raywa1cher.mucpollspring.models.sql;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class PollQuestionAnswer {
	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private Integer index;

	@ManyToOne(optional = false)
	private PollQuestion pollQuestion;

	@Column
	private String answer;
}
