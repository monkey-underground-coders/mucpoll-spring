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
@EqualsAndHashCode(of = {"id", "name"})
public class Tag {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private User creator;

	@ManyToMany(mappedBy = "tags", cascade = {CascadeType.PERSIST})
	private List<Poll> pollList;

	@Column
	private String name;
}
