package com.a6raywa1cher.mucpollspring.models.sql;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
@EqualsAndHashCode(of = {"id", "question", "index"})
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

	@Override
	public String toString() {
		return "PollQuestion{" +
				"id=" + id +
				", poll=" + poll.getId() +
				", index=" + index +
				", question='" + question + '\'' +
				", answerOptions=[" + answerOptions.stream()
				.map(PollQuestionAnswer::getId)
				.map(l -> l == null ? "null" : Long.toString(l))
				.reduce((a, b) -> a + ',' + b).orElse("") + ']' +
				'}';
	}
}
