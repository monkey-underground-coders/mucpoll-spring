package com.a6raywa1cher.mucpollspring.models.sql;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Poll {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String name;

	@OrderColumn
	@OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
	private List<PollQuestion> questions;

	@ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
	private User creator;

	@Enumerated(EnumType.ORDINAL)
	private PollStatus status;
}
