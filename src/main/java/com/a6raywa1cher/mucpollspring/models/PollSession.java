package com.a6raywa1cher.mucpollspring.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class PollSession {
	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(mappedBy = "pollSession")
	@OrderColumn
	private List<PollSessionQuestion> recordedQuestions;

	@Column
	private LocalDateTime startedAt;

	@Column
	private LocalDateTime recordedAt;
}
