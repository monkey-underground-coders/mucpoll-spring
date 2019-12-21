package com.a6raywa1cher.mucpollspring.models.sql;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
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
	private List<PollQuestion> questions;

	@ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
	private User creator;
}
