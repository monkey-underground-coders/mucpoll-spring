package com.a6raywa1cher.mucpollspring.models.sql;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class Poll {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String name;

	@OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
	private List<PollQuestion> questions = new ArrayList<>();

	@ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
	private User creator;

	@Column(nullable = false, columnDefinition = "int default 0")
	private int launchedCount;

	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable
	private List<Tag> tags = new ArrayList<>();

	@Override
	public String toString() {
		return "Poll{" +
				"id=" + id +
				", name='" + name + '\'' +
				", questions=[" + questions.stream()
				.map(PollQuestion::getId)
				.map(l -> Long.toString(l))
				.reduce((a, b) -> a + ',' + b).orElse("") + ']' +
				", creator=" + (creator != null ? creator.getId() : null) +
				'}';
	}
}
